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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuration for Cloudflare R2 storage bucket.
 * 
 * Required environment variables:
 * - CLOUDFLARE_R2_ENDPOINT: The R2 endpoint URL
 * - CLOUDFLARE_R2_BUCKET: The bucket name
 * - CLOUDFLARE_R2_ACCESS_KEY_ID: The access key ID
 * - CLOUDFLARE_R2_SECRET_ACCESS_KEY: The secret access key
 */
@Slf4j
@Configuration
public class StorageConfig {

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Value("${cloudflare.r2.access-key-id}")
    private String accessKeyId;

    @Value("${cloudflare.r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${cloudflare.r2.region:auto}")
    private String region;

    @Bean
    public S3Client s3Client() {
        log.info("Initializing S3 client for Cloudflare R2 storage bucket: {} at {}", bucketName, endpoint);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true) 
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region.equals("auto") ? "us-east-1" : region))
                .serviceConfiguration(s3Config)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        log.info("Initializing S3 presigner for Cloudflare R2 storage bucket at {}", endpoint);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region.equals("auto") ? "us-east-1" : region))
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
