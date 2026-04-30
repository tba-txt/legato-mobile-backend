package com.floriano.legato_api.services.CloudinaryService;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadFile_Success() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        String folderName = "test-folder";
        String expectedUrl = "http://cloudinary.com/test-folder/test.jpg";

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("secure_url", expectedUrl);

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(responseMap);

        String actualUrl = cloudinaryService.uploadFile(file, folderName);

        assertEquals(expectedUrl, actualUrl);
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadFile_IOException() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        String folderName = "test-folder";

        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("Upload failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cloudinaryService.uploadFile(file, folderName);
        });

        assertEquals("Erro ao fazer upload no Cloudinary", exception.getMessage());
    }

    @Test
    void deleteFile_Success_Image() throws Exception {
        String fileUrl = "http://res.cloudinary.com/dvrr02y8w/image/upload/v1714486400/legato/users/profile/1.jpg";
        String publicId = "legato/users/profile/1";

        when(uploader.destroy(eq(publicId), anyMap())).thenReturn(new HashMap<>());

        cloudinaryService.deleteFile(fileUrl);

        verify(uploader).destroy(eq(publicId), argThat(options -> "image".equals(options.get("resource_type"))));
    }

    @Test
    void deleteFile_Success_Video() throws Exception {
        String fileUrl = "http://res.cloudinary.com/dvrr02y8w/video/upload/v1714486400/legato/users/videos/1.mp4";
        String publicId = "legato/users/videos/1";

        when(uploader.destroy(eq(publicId), anyMap())).thenReturn(new HashMap<>());

        cloudinaryService.deleteFile(fileUrl);

        verify(uploader).destroy(eq(publicId), argThat(options -> "video".equals(options.get("resource_type"))));
    }

    @Test
    void deleteFile_NullOrEmptyUrl() {
        cloudinaryService.deleteFile(null);
        cloudinaryService.deleteFile(" ");
        cloudinaryService.deleteFile("string");

        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void deleteFile_Exception() throws Exception {
        String fileUrl = "http://res.cloudinary.com/dvrr02y8w/image/upload/v1714486400/legato/users/profile/1.jpg";
        String publicId = "legato/users/profile/1";

        when(uploader.destroy(eq(publicId), anyMap())).thenThrow(new RuntimeException("Deletion failed"));

        cloudinaryService.deleteFile(fileUrl);

        verify(uploader).destroy(eq(publicId), anyMap());
    }
}
