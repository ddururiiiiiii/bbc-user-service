package com.bookbookclub.bbc_user_service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 프로필 이미지 저장/삭제를 처리하는 서비스
 * - 저장: UUID + 원본 파일명 조합
 * - 삭제: URL로부터 파일명을 추출해 삭제
 */
@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private static final String PROFILE_IMAGE_DIR = "uploads/profile-images";
    private static final String BASE_URL = "/images/profile-images/";

    @Value("${custom.user.profile-image-upload-path}")
    private String uploadPath;

    /**
     * 프로필 이미지 저장
     * - 확장자: image/* 인지 확인
     * - 크기 제한: 5MB 이하
     * - UUID 기반 저장
     */
    public String store(MultipartFile file, Long userId) {

        // 확장자 검사
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        // 파일 크기 제한 (5MB 이하)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 용량은 5MB 이하만 가능합니다.");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path dirPath = Paths.get(PROFILE_IMAGE_DIR);
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path filePath = dirPath.resolve(filename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return BASE_URL + filename;
    }

    /**
     * 프로필 이미지 삭제
     * - 기본 이미지가 아닌 경우만 삭제 시도
     */
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        // 기본 이미지 경로가 아니라면 삭제 시도
        // 예: "/images/profile-images/파일명.png"
        if (imageUrl.startsWith("/images/profile-images/")) {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            File file = new File(uploadPath + File.separator + filename);
            if (file.exists()) {
                file.delete(); // 실패해도 예외 안 터지게 단순 시도만
            }
        }
    }
}
