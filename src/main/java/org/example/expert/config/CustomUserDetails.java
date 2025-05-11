package org.example.expert.config;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {  // UserDetails 구현 >> SecurityContext에 저장될 수 있는 형태

    private final Long userId;
    private final String email;
    private final String nickname;
    private final UserRole userRole;

    // 인증된 사용자 정보 표현
    // JWT 파싱 후, 사용자 정보를 담아서 객체를 만듦
    public CustomUserDetails(Long userId, String email, String nickname, UserRole userRole) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    // 권한 반환 (ROLE_ADMIN 이런식)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + userRole.name());
    }

    // 사용자 식별값 (email을 username으로 사용)
    @Override
    public String getUsername() {
        return email;
    }

    // jwt 기반 인증 구조에서는 비밀번호 인증을 하지 않기 때문에 null 반환
    @Override
    public String getPassword() {
        return null;
    }

    // 계정 관련 설정은 모두 true
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
