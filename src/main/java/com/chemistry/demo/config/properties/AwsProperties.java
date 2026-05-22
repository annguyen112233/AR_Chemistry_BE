package com.chemistry.demo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsProperties {

    private String accessKey;

    private String secretKey;

    private String region;

    private S3Properties s3;

    @Getter
    @Setter
    public static class S3Properties {

        private String bucketName;

        private String publicBaseUrl;
    }
}