package com.example.crmservice.service;

import com.example.crmservice.client.AwsS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final AwsS3Client s3Client;

    public String uploadPhoto(String existingPhotoId, MultipartFile photo) {
        deletePhoto(existingPhotoId);
        return s3Client.uploadNewPhoto(photo);
    }

    public String getPhotoUrlForPhotoId(String photoId) {
        return photoId != null
                ? s3Client.getPhotoUrl(photoId)
                : null;
    }

    public void deletePhoto(String existingPhotoId) {
        if (existingPhotoId != null && s3Client.doesPhotoExist(existingPhotoId)) s3Client.deletePhoto(existingPhotoId);
    }
}
