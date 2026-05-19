package com.chemistry.demo.services.ai;

import java.util.List;

/**
 * Abstraction cho việc tạo embedding vector từ text.
 */
public interface EmbeddingService {

    /**
     * Tạo embedding vector cho một đoạn text.
     * @param text nội dung cần embed
     * @return danh sách giá trị double đại diện cho vector embedding
     */
    List<Double> embed(String text);
}
