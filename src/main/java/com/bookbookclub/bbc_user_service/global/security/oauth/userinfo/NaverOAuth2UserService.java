package com.bookbookclub.bbc_user_service.global.security.oauth.userinfo;

import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.enums.AuthProvider;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * 네이버 OAuth2 사용자 정보 처리 서비스
 * - 네이버 응답: { "response": { "email": "...", "nickname": "...", ... } }
 */
@RequiredArgsConstructor
@Service
public class NaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 네이버 응답 구조: { "response": { "id": "...", "email": "...", "nickname": "..." } }
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");

        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");
        String providerId = (String) response.get("id");
        AuthProvider provider = AuthProvider.NAVER;

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    if (userRepository.existsByNickname(nickname)) {
                        throw new OAuth2AuthenticationException("이미 사용 중인 닉네임입니다.");
                    }
                    return userRepository.save(User.createSocialUser(email, nickname, provider, providerId));
                });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "nickname", user.getNickname()
                ),
                "email"
        );
    }
}
