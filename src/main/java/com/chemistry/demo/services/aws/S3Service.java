package com.chemistry.demo.services.aws;

/**
 * S3Service - Chỉ chứa logic thuần AWS S3.
 * Không chứa logic nghiệp vụ upload, validate, hay lưu DB.
 */
public interface S3Service {

    /**
     * Tạo presigned PUT URL để upload file lên S3.
     * 
     * @param key         S3 storage key (đường dẫn file trên S3)
     * @param contentType MIME type
     * @param fileSize    kích thước file (bytes)
     * @return presigned URL dạng String
     */
    String generatePresignedPutUrl(String key, String contentType, Long fileSize);

    /**
     * Build public file URL từ S3 key.
     * 
     * @param key S3 storage key
     * @return public URL
     */
    String buildFileUrl(String key);
}
