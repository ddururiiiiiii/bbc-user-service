package com.bookbookclub.bbc_user_service.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 사용자 관련 커스텀 설정들을 관리하는 클래스
 *
 * application-user.yml 의 custom.user.* 값을 바인딩함
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.user")
public class UserProperties {

    /**
     * 기본 프로필 이미지 경로
     * 예: /images/profile-images/default.jpg
     */
    private String defaultProfileImageUrl;

    /**
     * 업로드한 이미지 저장 경로 (로컬 경로)
     * 예: /Users/xxx/dev/bookbookclub/images/profile-images
     */
    private String profileImageUploadPath;

    /**
     * 업로드한 이미지를 URL로 접근할 때 사용하는 prefix
     * 예: /images/profile-images/
     */
    private String profileImageUrlPrefix;

    /**
     * 실제 파일이 저장될 로컬 상대 경로 (Spring Boot 프로젝트 기준)
     * 예: uploads/profile-images
     */
    private String profileImageLocalDir;

    /**
     * 허용 최대 이미지 파일 크기 (바이트 단위)
     * 예: 5242880 (5MB)
     */
    private long maxProfileImageSize;
}
