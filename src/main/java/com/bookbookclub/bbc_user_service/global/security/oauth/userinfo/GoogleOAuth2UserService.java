package com.bookbookclub.bbc_user_service.global.security.oauth.userinfo;


import com.bookbookclub.bbc_user_service.user.domain.User;
import com.bookbookclub.bbc_user_service.user.enums.AuthProvider;
import com.bookbookclub.bbc_user_service.user.repository.UserRepository;
import com.bookbookclub.common.security.CustomUserDetails;
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
 * 구글 OAuth2 사용자 정보 처리 서비스
 * - 이메일로 기존 사용자 조회
 * - 없으면 새 사용자 생성
 */
@RequiredArgsConstructor
@Service
public class GoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // sub: 구글 유저 고유 식별자
        // 이메일로 조회 → 없으면 닉네임 중복 체크 후 회원 생성
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub"); // 구글 ID
        AuthProvider provider = AuthProvider.GOOGLE;

        return userRepository.findByEmail(email)
                .map(user -> new DefaultOAuth2User(
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                        Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "nickname", user.getNickname()
                        ),
                        "email" // 👈 여기서 식별자 키 지정 (Principal로 쓸 key)
                ))
                .orElseGet(() -> {
                    if (userRepository.existsByNickname(name)) {
                        throw new OAuth2AuthenticationException("이미 사용 중인 닉네임입니다.");
                    }

                    User newUser = userRepository.save(User.createSocialUser(email, name, provider, providerId));

                    return new DefaultOAuth2User(
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + newUser.getRole().name())),
                            Map.of(
                                    "id", newUser.getId(),
                                    "email", newUser.getEmail(),
                                    "nickname", newUser.getNickname()
                            ),
                            "email"
                    );
                });
    }
}
