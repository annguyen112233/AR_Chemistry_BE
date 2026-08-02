package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Một đoạn (chunk) tri thức đã được vector hoá để phục vụ RAG.
 * Nguồn hiện tại: nội dung các bài học (Lesson). Mỗi chunk lưu kèm
 * embeddingJson để tính cosine similarity khi retrieve.
 */
@Entity
@Table(
        name = "knowledge_chunks",
        indexes = {
                @Index(name = "idx_knowledge_source", columnList = "source_type, source_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeChunk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Loại nguồn của chunk. Ví dụ: LESSON.
     */
    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    /**
     * Id của bản ghi nguồn (ví dụ Lesson.id).
     */
    @Column(name = "source_id", length = 64)
    private String sourceId;

    /**
     * Mã dễ đọc của nguồn để trích dẫn (ví dụ Lesson.lessonCode).
     */
    @Column(name = "source_code", length = 255)
    private String sourceCode;

    /**
     * Tiêu đề ngắn hiển thị khi trích dẫn nguồn cho model.
     */
    @Column(name = "title", length = 500)
    private String title;

    /**
     * Nội dung văn bản của chunk (đã được cắt nhỏ).
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * Vector embedding của content, serialize dạng JSON mảng double.
     */
    @Column(name = "embedding_json", columnDefinition = "TEXT")
    private String embeddingJson;
}
