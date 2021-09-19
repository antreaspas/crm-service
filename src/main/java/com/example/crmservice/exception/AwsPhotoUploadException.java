package com.example.crmservice.exception;

public class AwsPhotoUploadException extends RuntimeException {
    public AwsPhotoUploadException(Exception e) {
        super("Error while uploading photo to S3", e);
    }
}
