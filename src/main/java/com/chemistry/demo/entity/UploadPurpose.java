package com.chemistry.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "upload_purposes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadPurpose extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String folderPrefix;

    @Column(nullable = false)
    private Long maxFileSize; // in bytes

    @Column(nullable = false, length = 1000)
    private String allowedContentTypes; // comma separated formats e.g. "image/png,image/jpeg"

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}
