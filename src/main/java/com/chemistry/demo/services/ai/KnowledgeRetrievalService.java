package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.RetrievedChunk;

import java.util.List;

/**
 * Lõi RAG: index tri thức (bài học) thành vector và retrieve các đoạn
 * liên quan nhất cho một câu hỏi để grounding câu trả lời của LLM.
 */
public interface KnowledgeRetrievalService {

    /**
     * Vector hoá lại toàn bộ nội dung bài học vào kho tri thức.
     * @return số chunk đã index.
     */
    int reindexLessons();

    /**
     * Retrieve top-K chunk liên quan nhất tới câu hỏi.
     *
     * @param query    câu hỏi của người dùng
     * @param topK     số lượng chunk tối đa trả về
     * @param minScore ngưỡng cosine similarity tối thiểu để coi là liên quan
     */
    List<RetrievedChunk> retrieve(String query, int topK, double minScore);

    /**
     * Ghép các chunk retrieve được thành một khối ngữ cảnh có trích dẫn nguồn,
     * dùng để chèn vào prompt của LLM. Trả về chuỗi rỗng nếu không có chunk nào.
     */
    String buildContextBlock(List<RetrievedChunk> chunks);
}
