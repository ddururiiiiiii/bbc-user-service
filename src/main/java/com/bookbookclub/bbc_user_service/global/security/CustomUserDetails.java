package com.bookbookclub.bbc_user_service.global.security;

import com.bookbookclub.bbc_user_service.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Spring Security 및 OAuth2에서 사용할 사용자 정보 래퍼 클래스
 * - User 엔티티를 기반으로 권한 및 인증 정보 제공
 */
@Getter
public class CustomUserDetails implements UserDetails , OAuth2User {
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ROLE_접두어 붙인 권한 부여
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }


    @Override
    public String getPassword() {
        return user.getPassword(); // 로그인용일 때만 사용됨
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // unique key
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Long getId() {
        return user.getId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public User getUser() {
        return user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    public String getRole() {
        return user.getRole().name();
    }
}
