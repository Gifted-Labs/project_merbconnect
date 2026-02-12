package com.merbsconnect.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class R2ConfigRunner implements CommandLineRunner {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Override
    public void run(String... args) {
        log.info("Checking R2 Bucket Configuration for: {}", bucketName);
        configureCors();
    }

    private void configureCors() {
        try {
            log.info("Configuring CORS for bucket: {}", bucketName);

            List<CORSRule> corsRules = new ArrayList<>();

            // Allow GET and HEAD from anywhere for public access (or restrict to your
            // domain)
            CORSRule publicReadRule = CORSRule.builder()
                    .allowedMethods(("GET"), ("HEAD"))
                    .allowedOrigins("*") // TODO: Restrict this to your frontend domain in production if needed
                    .allowedHeaders("*")
                    .maxAgeSeconds(3000)
                    .build();
            corsRules.add(publicReadRule);

            // Allow PUT, POST, DELETE from your frontend/dashboard domain if intended for
            // uploads
            // For now, let's keep it simple and open for dev/debugging
            CORSRule uploadRule = CORSRule.builder()
                    .allowedMethods("PUT", "POST", "DELETE")
                    .allowedOrigins("*")
                    .allowedHeaders("*")
                    .maxAgeSeconds(3000)
                    .build();
            corsRules.add(uploadRule);

            CORSConfiguration corsConfiguration = CORSConfiguration.builder()
                    .corsRules(corsRules)
                    .build();

            PutBucketCorsRequest putBucketCorsRequest = PutBucketCorsRequest.builder()
                    .bucket(bucketName)
                    .corsConfiguration(corsConfiguration)
                    .build();

            s3Client.putBucketCors(putBucketCorsRequest);
            log.info("Successfully configured CORS for R2 bucket: {}", bucketName);

        } catch (S3Exception e) {
            if (e.statusCode() == 403) {
                log.warn(
                        "Access Denied when configuring CORS for bucket: {}. This is expected if using a restricted API Token. Assuming CORS is already configured.",
                        bucketName);
            } else {
                log.error("Failed to configure CORS for bucket: {}. Error: {}", bucketName, e.getMessage());
            }
            // Don't throw exception to avoid preventing app startup, just log error
        }
    }
}
