package com.chemistry.demo.services.ai.Impl;

import com.chemistry.demo.dto.ai.RetrievedChunk;
import com.chemistry.demo.entity.KnowledgeChunk;
import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.repository.KnowledgeChunkRepository;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.services.ai.EmbeddingService;
import com.chemistry.demo.services.ai.KnowledgeRetrievalService;
import com.chemistry.demo.services.ai.SimilarityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Triển khai RAG dựa trên embedding + cosine similarity trong bộ nhớ,
 * đồng bộ với cách ConversationMemoryService đang làm (giữ đơn giản,
 * không cần vector DB riêng cho quy mô tài liệu hiện tại).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeRetrievalServiceImpl implements KnowledgeRetrievalService {

    private static final String SOURCE_LESSON = "LESSON";
    private static final int MAX_CHUNK_CHARS = 800;
    private static final int CHUNK_OVERLAP_CHARS = 120;
    private static final int MAX_CONTEXT_CHARS = 3000;

    private final LessonRepository lessonRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final EmbeddingService embeddingService;
    private final SimilarityService similarityService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public int reindexLessons() {
        log.info("Reindexing lessons into knowledge base...");
        knowledgeChunkRepository.deleteBySourceType(SOURCE_LESSON);

        List<Lesson> lessons = lessonRepository.findAll();
        List<KnowledgeChunk> toSave = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (!isActive(lesson)) continue;

            String body = firstNonBlank(lesson.getCleanContent(), lesson.getContent(), lesson.getSummary());
            if (body == null || body.isBlank()) continue;

            String header = buildHeader(lesson);
            List<String> pieces = chunk(body);

            for (String piece : pieces) {
                String chunkText = header + piece;
                List<Double> embedding = embeddingService.embed(chunkText);
                if (embedding.isEmpty()) {
                    log.warn("Skipping chunk of lesson {} (embedding failed)", lesson.getLessonCode());
                    continue;
                }
                toSave.add(KnowledgeChunk.builder()
                        .sourceType(SOURCE_LESSON)
                        .sourceId(lesson.getId())
                        .sourceCode(lesson.getLessonCode())
                        .title(lesson.getTitle())
                        .content(chunkText)
                        .embeddingJson(serialize(embedding))
                        .build());
            }
        }

        knowledgeChunkRepository.saveAll(toSave);
        log.info("Knowledge base reindexed: {} chunks from {} lessons", toSave.size(), lessons.size());
        return toSave.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetrievedChunk> retrieve(String query, int topK, double minScore) {
        if (query == null || query.isBlank()) return List.of();

        List<Double> queryEmbedding = embeddingService.embed(query);
        if (queryEmbedding.isEmpty()) {
            log.warn("Query embedding failed, skipping retrieval");
            return List.of();
        }

        List<KnowledgeChunk> chunks = knowledgeChunkRepository.findByEmbeddingJsonIsNotNull();
        if (chunks.isEmpty()) {
            log.info("Knowledge base is empty, nothing to retrieve");
            return List.of();
        }

        List<RetrievedChunk> scored = new ArrayList<>();
        for (KnowledgeChunk chunk : chunks) {
            List<Double> embedding = deserialize(chunk.getEmbeddingJson());
            if (embedding.isEmpty()) continue;

            double score = similarityService.cosineSimilarity(queryEmbedding, embedding);
            if (score < minScore) continue;

            scored.add(RetrievedChunk.builder()
                    .sourceType(chunk.getSourceType())
                    .sourceCode(chunk.getSourceCode())
                    .title(chunk.getTitle())
                    .content(chunk.getContent())
                    .score(score)
                    .build());
        }

        scored.sort(Comparator.comparingDouble(RetrievedChunk::score).reversed());
        List<RetrievedChunk> top = scored.size() > topK ? scored.subList(0, topK) : scored;
        log.info("RAG retrieval: {} chunks above threshold {} (returning top {})",
                scored.size(), minScore, top.size());
        return new ArrayList<>(top);
    }

    @Override
    public String buildContextBlock(List<RetrievedChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (RetrievedChunk chunk : chunks) {
            String label = chunk.title() != null ? chunk.title() : chunk.sourceCode();
            String entry = "[Nguồn " + index + " - " + label + "]\n" + chunk.content().strip() + "\n\n";
            if (sb.length() + entry.length() > MAX_CONTEXT_CHARS) break;
            sb.append(entry);
            index++;
        }
        return sb.toString().strip();
    }

    // ---------- helpers ----------

    private boolean isActive(Lesson lesson) {
        return lesson.getStatus() == null || lesson.getStatus().isBlank()
                || "active".equalsIgnoreCase(lesson.getStatus());
    }

    private String buildHeader(Lesson lesson) {
        StringBuilder header = new StringBuilder();
        if (lesson.getTitle() != null) header.append(lesson.getTitle());
        if (lesson.getChapter() != null && !lesson.getChapter().isBlank()) {
            header.append(" (").append(lesson.getChapter()).append(")");
        }
        if (header.length() > 0) header.append(":\n");
        return header.toString();
    }

    /**
     * Cắt văn bản thành các đoạn tối đa MAX_CHUNK_CHARS ký tự, có overlap
     * để không mất ngữ cảnh ở biên đoạn.
     */
    private List<String> chunk(String text) {
        String normalized = text.replaceAll("\\s+", " ").strip();
        List<String> pieces = new ArrayList<>();
        if (normalized.length() <= MAX_CHUNK_CHARS) {
            pieces.add(normalized);
            return pieces;
        }
        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + MAX_CHUNK_CHARS, normalized.length());
            pieces.add(normalized.substring(start, end));
            if (end == normalized.length()) break;
            start = end - CHUNK_OVERLAP_CHARS;
        }
        return pieces;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }

    private String serialize(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            log.error("Failed to serialize embedding: {}", e.getMessage());
            return null;
        }
    }

    private List<Double> deserialize(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize embedding: {}", e.getMessage());
            return List.of();
        }
    }
}
