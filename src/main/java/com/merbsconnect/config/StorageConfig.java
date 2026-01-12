package com.merbsconnect.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Configuration for Railway S3-compatible storage bucket.
 * 
 * Required environment variables:
 * - RAILWAY_STORAGE_ENDPOINT: The S3 endpoint URL (e.g.,
 * https://storage.railway.app)
 * - RAILWAY_STORAGE_BUCKET: The bucket name
 * - RAILWAY_STORAGE_ACCESS_KEY_ID: The access key ID
 * - RAILWAY_STORAGE_SECRET_ACCESS_KEY: The secret access key
 */
@Slf4j
@Configuration
public class StorageConfig {

    @Value("${railway.storage.endpoint:https://storage.railway.app}")
    private String endpoint;

    @Value("${railway.storage.bucket:functional-case-wjpzk8lgw}")
    private String bucketName;

    @Value("${railway.storage.access-key-id:}")
    private String accessKeyId;

    @Value("${railway.storage.secret-access-key:}")
    private String secretAccessKey;

    @Value("${railway.storage.region:auto}")
    private String region;

    @Bean
    public S3Client s3Client() {
        log.info("Initializing S3 client for Railway storage bucket: {} at {}", bucketName, endpoint);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true) // Required for S3-compatible services
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region.equals("auto") ? "us-east-1" : region)) // Railway uses auto region
                .serviceConfiguration(s3Config)
                .build();
    }

    @Bean
    public String storageBucketName() {
        return bucketName;
    }

    @Bean
    public String storageEndpoint() {
        return endpoint;
    }
}
