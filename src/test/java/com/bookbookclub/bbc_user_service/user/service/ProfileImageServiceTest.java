package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.user.config.UserProperties;
import com.bookbookclub.bbc_user_service.user.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileImageServiceTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Mock
    private UserProperties userProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // 공통적으로 진짜 필요한 것만 여기에 둠
    }

    @Test
    void 이미지_용량_초과시_예외() {
        byte[] big = new byte[3_000_000];
        MultipartFile bigFile = new MockMultipartFile("file", "big.png", "image/png", big);

        when(userProperties.getMaxProfileImageSize()).thenReturn(2_000_000L);

        assertThrows(AuthException.class, () -> profileImageService.store(bigFile));
    }

    @Test
    void 기본_이미지인지_확인() {
        when(userProperties.getDefaultProfileImageUrl()).thenReturn("http://localhost:8080/images/default.png");

        assertTrue(profileImageService.isDefaultImage("http://localhost:8080/images/default.png"));
        assertFalse(profileImageService.isDefaultImage("http://localhost:8080/images/not-default.png"));
    }

    @Test
    void 이미지_저장_성공() {
        byte[] data = new byte[1000];
        MultipartFile file = new MockMultipartFile("file", "profile.png", "image/png", data);

        when(userProperties.getMaxProfileImageSize()).thenReturn(2_000_000L);
        when(userProperties.getProfileImageUploadPath()).thenReturn(tempDir.toString() + "/");
        when(userProperties.getProfileImageUrlPrefix()).thenReturn("http://localhost:8080/images/");

        String result = profileImageService.store(file);

        assertTrue(result.startsWith("http://localhost:8080/images/"));
    }

    @Test
    void 잘못된_확장자일_때_예외() {
        MultipartFile badFile = new MockMultipartFile("file", "x.exe", "application/octet-stream", new byte[100]);

        assertThrows(AuthException.class, () -> profileImageService.store(badFile));
    }

    @Test
    void 없는_파일_삭제해도_예외_안남() {
        when(userProperties.getProfileImageUrlPrefix()).thenReturn("http://localhost:8080/images/");
        String url = "http://localhost:8080/images/not-exist.png";

        assertDoesNotThrow(() -> profileImageService.delete(url));
    }

    @Test
    void 실제_파일_삭제_정상작동() throws IOException {
        String uploadDir = tempDir.toString() + "/";
        String fileName = "test-delete.png";
        String prefix = "http://localhost:8080/images/";

        when(userProperties.getProfileImageUploadPath()).thenReturn(uploadDir);
        when(userProperties.getProfileImageUrlPrefix()).thenReturn(prefix);

        File file = new File(uploadDir, fileName);
        assertTrue(file.createNewFile()); // 존재 확인
        assertTrue(file.exists());

        String imageUrl = prefix + fileName;
        profileImageService.delete(imageUrl);

        assertFalse(file.exists());
    }


}
