package com.chemistry.demo.config.seeder;

import com.chemistry.demo.aspect.NoLogging;
import com.chemistry.demo.entity.UploadPurpose;
import com.chemistry.demo.repository.UploadPurposeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@NoLogging
@Component
@RequiredArgsConstructor
public class UploadPurposeSeeder implements DataSeeder {

    private final UploadPurposeRepository uploadPurposeRepository;

    @Override
    public void seed() {
        List<UploadPurpose> purposes = Arrays.asList(
                UploadPurpose.builder()
                        .code("FEEDBACK")
                        .folderPrefix("feedback")
                        .maxFileSize(10 * 1024 * 1024L)
                        .allowedContentTypes("image/png,image/jpeg,application/pdf")
                        .active(true)
                        .build(),
                UploadPurpose.builder()
                        .code("EXERCISE_IMPORT")
                        .folderPrefix("exercise-import")
                        .maxFileSize(50 * 1024 * 1024L)
                        .allowedContentTypes(
                                "application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,image/png,image/jpeg,text/plain")
                        .active(true)
                        .build(),
                UploadPurpose.builder()
                        .code("AVATAR")
                        .folderPrefix("avatars")
                        .maxFileSize(5 * 1024 * 1024L)
                        .allowedContentTypes("image/png,image/jpeg")
                        .active(true)
                        .build(),
                UploadPurpose.builder()
                        .code("DOCUMENT")
                        .folderPrefix("documents")
                        .maxFileSize(30 * 1024 * 1024L)
                        .allowedContentTypes(
                                "application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .active(true)
                        .build(),
                UploadPurpose.builder()
                        .code("QUIZ_IMPORT")
                        .folderPrefix("quizCSV-import")
                        .maxFileSize(10 * 1024 * 1024L)
                        .allowedContentTypes("text/csv,application/vnd.ms-excel")
                        .active(true)
                        .build(),
                UploadPurpose.builder()
                        .code("SINGLE_CARD_QR")
                        .folderPrefix("single-cards")
                        .maxFileSize(5 * 1024 * 1024L)
                        .allowedContentTypes("image/png,image/jpeg,image/webp")
                        .active(true)
                        .build()
                );


        for (UploadPurpose purpose : purposes) {
            uploadPurposeRepository.findByCodeAndActiveTrue(purpose.getCode())
                    .orElseGet(() -> uploadPurposeRepository.save(purpose));
        }
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
