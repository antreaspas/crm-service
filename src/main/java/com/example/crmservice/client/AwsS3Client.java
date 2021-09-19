package com.example.crmservice.client;

import com.example.crmservice.config.properties.AwsConfigProperties;
import com.example.crmservice.exception.AwsPhotoUploadException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class AwsS3Client {

    private final S3Client client;
    private final String bucketName;
    private final Region region;
    private final S3Presigner presigner;
    private final int presignedUrlExpiryInMinutes;

    public AwsS3Client(AwsConfigProperties properties) {
        this.bucketName = properties.getBucketName();
        this.region = Region.of(properties.getRegion());
        this.presignedUrlExpiryInMinutes = properties.getPresignedUrlExpiryInMinutes();
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey())
        );
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .region(region)
                .credentialsProvider(credentials);
        S3Presigner.Builder presignerBuilder = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentials);
        if (Strings.isNotBlank(properties.getEndpoint())) {
            // Override for localstack
            URI endpoint = URI.create(properties.getEndpoint());
            s3ClientBuilder.endpointOverride(endpoint);
            presignerBuilder.endpointOverride(endpoint);
        }
        this.client = s3ClientBuilder.build();
        this.presigner = presignerBuilder.build();
    }

    public void createBucket() {
        if (!doesBucketExist()) {
            log.info("Creating AWS S3 Bucket {} in region {}", bucketName, region);
            client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(CreateBucketConfiguration.builder()
                            .locationConstraint(region.id())
                            .build())
                    .build());
            client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("AWS S3 Bucket {} in region {} created and is ready to use", bucketName, region);
        } else {
            log.info("AWS S3 Bucket {} already exists in region {}", bucketName, region);
        }
    }

    public void deletePhoto(String photoId) {
        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(photoId)
                .build());
        client.waiter().waitUntilObjectNotExists(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(photoId)
                .build());
    }

    public boolean doesPhotoExist(String photoId) {
        try {
            client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(photoId)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    public String uploadNewPhoto(MultipartFile multipartFile) {
        // Preserve original filename
        String photoId = String.format("%s/%s", UUID.randomUUID(), multipartFile.getOriginalFilename());
        try {
            client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(photoId)
                            .build(),
                    RequestBody.fromBytes(multipartFile.getBytes()));
        } catch (IOException e) {
            throw new AwsPhotoUploadException(e);
        }
        client.waiter().waitUntilObjectExists(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(photoId)
                .build());
        return photoId;
    }

    public String getPhotoUrl(String photoId) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(photoId)
                        .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlExpiryInMinutes))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    private boolean doesBucketExist() {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

}
