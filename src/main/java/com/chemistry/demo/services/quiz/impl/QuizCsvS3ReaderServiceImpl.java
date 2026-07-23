package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.config.properties.AwsProperties;
import com.chemistry.demo.dto.quizCSV.QuizCsvRow;
import com.chemistry.demo.services.quiz.QuizCsvS3ReaderService;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizCsvS3ReaderServiceImpl
        implements QuizCsvS3ReaderService {

    private final AwsProperties awsProperties;
    private final S3Client s3Client;

    @Override
    public List<QuizCsvRow> readCsvFromS3(
            String s3Key
    ) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new IllegalArgumentException(
                    "s3Key is required"
            );
        }

        try {
            GetObjectRequest request =
                    GetObjectRequest.builder()
                            .bucket(
                                    awsProperties
                                            .getS3()
                                            .getBucketName()
                            )
                            .key(s3Key.trim())
                            .build();

            try (
                    ResponseInputStream<GetObjectResponse>
                            inputStream =
                            s3Client.getObject(request);

                    BOMInputStream bomInputStream =
                            BOMInputStream.builder()
                                    .setInputStream(inputStream)
                                    .get();

                    Reader reader =
                            new InputStreamReader(
                                    bomInputStream,
                                    StandardCharsets.UTF_8
                            )
            ) {
                List<QuizCsvRow> rows =
                        new CsvToBeanBuilder<QuizCsvRow>(
                                reader
                        )
                                .withType(QuizCsvRow.class)
                                .withIgnoreLeadingWhiteSpace(true)
                                .withIgnoreEmptyLine(true)
                                .withThrowExceptions(true)
                                .build()
                                .parse();

                if (rows == null || rows.isEmpty()) {
                    throw new IllegalStateException(
                            "CSV file contains no quiz questions"
                    );
                }

                return rows;
            }

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Read quiz CSV from S3 failed for key "
                            + s3Key
                            + ": "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}