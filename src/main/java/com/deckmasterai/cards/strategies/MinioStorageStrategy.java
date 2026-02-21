package com.deckmasterai.cards.strategies;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Primary
public class MinioStorageStrategy implements FileStorageStrategy {

    private final S3Client s3Client;
    private final String bucket;

    public MinioStorageStrategy(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Override
    
    public String upload(MultipartFile file) {

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    @Override
    public void delete(String fileName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build()
        );
    }
}
