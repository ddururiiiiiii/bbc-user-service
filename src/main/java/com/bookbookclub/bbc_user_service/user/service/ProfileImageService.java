package com.bookbookclub.bbc_user_service.user.service;

import com.bookbookclub.bbc_user_service.user.config.UserProperties;
import com.bookbookclub.bbc_user_service.user.exception.AuthException;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

/**
 * 프로필 이미지 저장 및 삭제를 담당하는 서비스
 * - 이미지 타입 및 크기 검증
 * - UUID 기반 파일명으로 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final UserProperties userProperties;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 프로필 이미지 저장
     */
    public String store(MultipartFile file) {
        validateImage(file);

        String filename = generateStoredFileName(file);
        Path dirPath = Paths.get(userProperties.getProfileImageUploadPath());
        Path filePath = dirPath.resolve(filename);

        try {
            createDirectoryIfNotExists(dirPath);
            saveFile(file, filePath);
        } catch (IOException e) {
            throw new AuthException(UserErrorCode.PROFILE_IMAGE_UPLOAD_FAIL);
        }

        return userProperties.getProfileImageUrlPrefix() + filename;
    }

    /**
     * 프로필 이미지 삭제
     */
    public void delete(String imageUrl) {
        if (!hasText(imageUrl)) return;
        if (isDefaultImage(imageUrl)) return;

        String filename = extractFilename(imageUrl);
        File file = new File(userProperties.getProfileImageUploadPath(), filename);
        if (file.exists()) {
            if (!file.delete()) {
                log.warn("프로필 이미지 삭제 실패: {}", file.getAbsolutePath());
                throw new AuthException(UserErrorCode.PROFILE_IMAGE_DELETE_FAIL);
            }
        }
    }

    /**
     * 기본 이미지 URL 반환
     */
    public String getDefaultImageUrl() {
        return userProperties.getDefaultProfileImageUrl();
    }

    /**
     * 기본 이미지 여부 확인
     */
    public boolean isDefaultImage(String imageUrl) {
        return imageUrl != null && imageUrl.equals(getDefaultImageUrl());
    }

    // 이미지 유효성 검사 (용량, 확장자)
    private void validateImage(MultipartFile file) {
        // 최대 용량 초과 검사
        if (file.getSize() > userProperties.getMaxProfileImageSize()) {
            throw new AuthException(UserErrorCode.PROFILE_IMAGE_TOO_LARGE);
        }

        try (InputStream input = file.getInputStream()) {
            // 확장자 화이트리스트 검사
            String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                throw new AuthException(UserErrorCode.INVALID_PROFILE_IMAGE_TYPE);
            }
        } catch (IOException e) {
            throw new AuthException(UserErrorCode.INVALID_PROFILE_IMAGE_TYPE);
        }
    }

    // UUID + 정제된 파일명으로 저장용 파일명 생성
    // ex) "내 프로필 이미지.png" → "550e8400-e29b-41d4-a716-446655440000_naegongilji.png"
    private String generateStoredFileName(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase(); // 이미 유효하다고 가정
        String originalName = FilenameUtils.getBaseName(file.getOriginalFilename());
        originalName = originalName.replaceAll("[^a-zA-Z0-9-_]", "");

        return UUID.randomUUID() + "_" + originalName + "." + extension;
    }

    // 디렉토리가 없으면 생성
    // ex) "./uploads/images/" 없을 경우 생성
    private void createDirectoryIfNotExists(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    // 파일 저장 (MultipartFile → 로컬 경로)
    // ex) file → "./uploads/images/uuid_filename.png"
    private void saveFile(MultipartFile file, Path filePath) throws IOException {
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // 이미지 URL에서 파일명 추출 (URL 검증 포함)
    // ex) "http://localhost:8080/images/abc123.png" → "abc123.png"
    // 잘못된 형식이면 예외
    private String extractFilename(String imageUrl) {
        int idx = imageUrl.lastIndexOf("/");
        if (idx == -1 || idx == imageUrl.length() - 1) {
            throw new AuthException(UserErrorCode.INVALID_PROFILE_IMAGE_URL);
        }
        if (!imageUrl.startsWith(userProperties.getProfileImageUrlPrefix())) {
            throw new AuthException(UserErrorCode.INVALID_PROFILE_IMAGE_URL);
        }
        return imageUrl.substring(idx + 1);

    }


}
