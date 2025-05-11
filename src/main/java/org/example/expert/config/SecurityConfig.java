package org.example.expert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // Spring 설정 클래스
@EnableWebSecurity // Spring Security 활성화
public class SecurityConfig {  //  JWT 기반 인증 시스템의 설정 파일

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) { // jwtUtil을 주입받아, jwtAuthenticationFilter 객체를 생성하고 빈으로 등록
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())   // jwt 기반에서는 csrf 보호가 필요하지 않으므로, 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // jwt는 서버가 세션을 저장하지 않기 때문에 stateless 인증으로 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()         // 로그인/회원가입은 인증없이 누구나 접근
                .requestMatchers("/admin/**").hasRole("ADMIN")  // ROLE_ADMIN 권한 필요
                .anyRequest().authenticated()                   // 나머지 경로는 인증 필요
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // 인증 없을때 401 에러
                .accessDeniedHandler(new JwtAccessDeniedHandler())           // 인증은 있지만 권한 부족하면 403 에러
            )
            .addFilterBefore(jwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)  // JWT 필터를 로그인 필터(UsernamePasswordAuthenticationFilter)보다 먼저 실행시켜줘. 그래야 나머지 필터들이 SecurityContext에 인증된 사용자를 보고 작동할 수 있음.
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
