package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 웹 어플리케이션에서 가장 먼저 실행되는 Filter 체인.
// 서블릿 필터 체인에서 실행 (DispatcherServlet보다 먼저 실행됨)
@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtUtil jwtUtil;

    // JwtFilter 등록
    // Servlet 기반의 일반 필터를 FilterRegistrationBean으로 등록하여 모든 요청을 수동 처리
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter(jwtUtil));  // setFilter : 사용할 필터 지정 >> 커스텀 JwtFilter 등록
        registrationBean.addUrlPatterns("/*"); // addUrlPatterns : 필터를 적용할 URL 패턴을 매핑 >> 모든 url 요청에 대하여 필터 매핑

        return registrationBean;
    }
}
