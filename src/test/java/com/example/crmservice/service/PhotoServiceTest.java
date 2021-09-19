package com.example.crmservice.service;

import com.example.crmservice.client.AwsS3Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.crmservice.utils.TestUtils.generateMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {
    @Mock
    private AwsS3Client s3Client;
    @InjectMocks
    private PhotoService photoService;

    @Test
    public void testGetPhotoUrlForPhotoId() {
        when(s3Client.getPhotoUrl("PHOTO ID")).thenReturn("PHOTO URL");
        assertThat(photoService.getPhotoUrlForPhotoId("PHOTO ID")).isEqualTo("PHOTO URL");
    }

    @Test
    public void testGetPhotoUrlForPhotoIdReturnsNullWhenPhotoIdIsNull() {
        assertThat(photoService.getPhotoUrlForPhotoId(null)).isNull();
    }

    @Test
    public void testDeletePhoto() {
        when(s3Client.doesPhotoExist("PHOTO ID")).thenReturn(true);
        photoService.deletePhoto("PHOTO ID");
        verify(s3Client).deletePhoto("PHOTO ID");
    }

    @Test
    public void testDeletePhotoDoesNotCallS3ClientWhenPhotoIdIsNull() {
        photoService.deletePhoto(null);
        verify(s3Client, never()).deletePhoto(any());
    }

    @Test
    public void testDeletePhotoDoesNotCallS3ClientWhenPhotoIdDoesNotExistInS3() {
        when(s3Client.doesPhotoExist("PHOTO ID")).thenReturn(false);
        photoService.deletePhoto("PHOTO ID");
        verify(s3Client, never()).deletePhoto(any());
    }

    @Test
    public void testUploadPhotoDeletesExistingPhotoAndReturnsUrl() {
        when(s3Client.doesPhotoExist("PHOTO ID")).thenReturn(true);
        when(s3Client.uploadNewPhoto(any())).thenReturn("PHOTO URL");
        assertThat(photoService.uploadPhoto("PHOTO ID", generateMockMultipartFile())).isEqualTo("PHOTO URL");
        verify(s3Client).deletePhoto("PHOTO ID");
    }
}
