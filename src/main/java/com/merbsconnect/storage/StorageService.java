package com.merbsconnect.storage;

import com.merbsconnect.enums.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for uploading and managing files in Railway S3-compatible storage
 * bucket.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3Client;

    @Qualifier("storageBucketName")
    private final String bucketName;

    @Qualifier("storageEndpoint")
    private final String endpoint;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/webm", "video/quicktime");

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    /**
     * Uploads a file to the storage bucket for an event gallery.
     *
     * @param eventId   The event ID
     * @param file      The file to upload
     * @param mediaType The type of media (IMAGE or VIDEO)
     * @return The public URL of the uploaded file
     */
    public String uploadGalleryItem(Long eventId, MultipartFile file, MediaType mediaType) throws IOException {
        validateFile(file, mediaType);

        String key = generateKey(eventId, file.getOriginalFilename(), mediaType);
        String contentType = file.getContentType();

        log.info("Uploading file to bucket: {} with key: {}", bucketName, key);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        String publicUrl = generatePublicUrl(key);
        log.info("File uploaded successfully: {}", publicUrl);

        return publicUrl;
    }

    /**
     * Deletes a file from the storage bucket.
     *
     * @param key The object key in the bucket
     */
    public void deleteFile(String key) {
        log.info("Deleting file from bucket: {} with key: {}", bucketName, key);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("File deleted successfully: {}", key);
    }

    /**
     * Extracts the object key from a full URL.
     *
     * @param url The full URL of the object
     * @return The object key
     */
    public String extractKeyFromUrl(String url) {
        // URL format: https://storage.railway.app/bucket-name/path/to/file
        String prefix = endpoint + "/" + bucketName + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return url;
    }

    /**
     * Validates the uploaded file.
     */
    private void validateFile(MultipartFile file, MediaType mediaType) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        long fileSize = file.getSize();

        if (mediaType == MediaType.IMAGE) {
            if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("Invalid image type: " + contentType +
                        ". Allowed types: " + ALLOWED_IMAGE_TYPES);
            }
            if (fileSize > MAX_IMAGE_SIZE) {
                throw new IllegalArgumentException("Image size exceeds maximum of 10MB");
            }
        } else if (mediaType == MediaType.VIDEO) {
            if (!ALLOWED_VIDEO_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("Invalid video type: " + contentType +
                        ". Allowed types: " + ALLOWED_VIDEO_TYPES);
            }
            if (fileSize > MAX_VIDEO_SIZE) {
                throw new IllegalArgumentException("Video size exceeds maximum of 100MB");
            }
        }
    }

    /**
     * Generates a unique key for the file in the bucket.
     */
    private String generateKey(Long eventId, String originalFilename, MediaType mediaType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);
        String folder = mediaType == MediaType.IMAGE ? "images" : "videos";

        return String.format("events/%d/gallery/%s/%s_%s.%s",
                eventId, folder, timestamp, uuid, extension);
    }

    /**
     * Gets the file extension from a filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Generates the public URL for an object.
     * Since Railway buckets are private, this returns a path-style URL.
     * For public access, presigned URLs should be used.
     */
    private String generatePublicUrl(String key) {
        return String.format("%s/%s/%s", endpoint, bucketName, key);
    }

    /**
     * Checks if the bucket is accessible.
     */
    public boolean isBucketAccessible() {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(request);
            return true;
        } catch (Exception e) {
            log.error("Bucket not accessible: {}", bucketName, e);
            return false;
        }
    }
}
