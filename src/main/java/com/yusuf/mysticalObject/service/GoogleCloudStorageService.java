package com.yusuf.mysticalObject.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GoogleCloudStorageService {
    private static final Logger log = LoggerFactory.getLogger(GoogleCloudStorageService.class);
    @Value("${gcp.bucket.name}")
    private String bucketName;
    private final Storage storage;

    @Autowired
    public GoogleCloudStorageService(Environment env) {
        try {
            this.bucketName = env.getProperty("gcp.bucket.name", "mysticalobjectemporium");
            log.info("Initializing Google Cloud Storage with bucket: {}", bucketName);

            // Credentials dosyasının yolunu kontrol et
            String credentialsPath = env.getProperty("spring.cloud.gcp.credentials.location");
            log.info("Loading credentials from: {}", credentialsPath);

            StorageOptions options = StorageOptions.newBuilder()
                    .setProjectId(env.getProperty("spring.cloud.gcp.project-id"))
                    .setCredentials(GoogleCredentials.fromStream(
                            getClass().getResourceAsStream("/mysticalobjectemporium-444521-88fda94ddf96.json")
                    ))
                    .build();

            this.storage = options.getService();
            log.info("Storage service initialized with project: {}", options.getProjectId());

            // Test connection
            Bucket bucket = storage.get(bucketName);
            log.info("Successfully connected to bucket: {}", bucket.getName());
        } catch (Exception e) {
            log.error("Failed to initialize storage: ", e);
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            log.info("Attempting to upload file: {}", file.getOriginalFilename());

            // Upload işlemi öncesi credentials'ı tekrar kontrol et
            log.info("Current credentials: {}", storage.getOptions().getCredentials());

            String fileName = generateFileName(file.getOriginalFilename());
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("Could not upload file", e);
        }
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getExtension(originalFileName);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            BlobId blobId = BlobId.of(bucketName, fileName);
            storage.delete(blobId);
        } catch (Exception e) {
            log.error("Error deleting file", e);
            throw new RuntimeException("Could not delete file", e);
        }
    }
} 