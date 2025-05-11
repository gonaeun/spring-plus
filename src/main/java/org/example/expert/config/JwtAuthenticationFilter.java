package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {   // Spring Security 필터가 요청당 한번만 실행되도록

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 jwt 추출
        String bearerToken = request.getHeader("Authorization");

        // 토큰 값이 있고, Bearer 접두사로 시작하는 경우에만 실행
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // JWT 파싱해서 Claims 추출
            String jwt = jwtUtil.substringToken(bearerToken);

            try {
                Claims claims = jwtUtil.extractClaims(jwt);

                Long userId = Long.parseLong(claims.getSubject());
                String email = claims.get("email", String.class);
                String nickname = claims.get("nickname", String.class);
                String role = claims.get("userRole", String.class);

                // Spring Security의 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(userId, email, nickname, UserRole.valueOf(role)),
                        null,
                        Collections.singleton(() -> "ROLE_" + role)  // 세번째 인자는 권한목록(ROLE_USER, ROLE_ADMIN)
                    );

                // 요청 기반의 부가 정보 설정(ip, 세션id 등)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);  // 요기에!!! SecurityContext에 인증 객체 저장. 앞으로 이걸 기준으로 인증/인가 처리될거임

            } catch (Exception e) {
                log.error("JWT 필터 처리 중 오류 발생", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT입니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}