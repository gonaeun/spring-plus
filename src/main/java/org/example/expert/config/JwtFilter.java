//package org.example.expert.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.UnsupportedJwtException;
//import jakarta.servlet.FilterConfig;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.expert.domain.user.enums.UserRole;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//public class JwtFilter implements Filter {  // JWT 인증 처리하는 커스텀 필터
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        String url = httpRequest.getRequestURI();
//
//        // "/auth"로 시작되는 경로는 필터 패스(인증x)
//        if (url.startsWith("/auth")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // JWT 헤더에서 토큰 추출
//        String bearerJwt = httpRequest.getHeader("Authorization");
//
//        if (bearerJwt == null) {
//            // 토큰이 없는 경우 400을 반환
//            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
//            return;
//        }
//
//        // Bearer 접두사 제거해서 실제 토큰 문자열 반환 (JwtUtil.substringToken())
//        String jwt = jwtUtil.substringToken(bearerJwt);
//
//        try {
//            // JWT 유효성 검사와 claims 추출
//            Claims claims = jwtUtil.extractClaims(jwt);
//            if (claims == null) {
//                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
//                return;
//            }
//
//            // payload에서 인증된 유저 정보를 꺼내서 request에 저장
//            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));
//            String nickname = claims.get("nickname", String.class); // 닉네임 추출해서
//
//            httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
//            httpRequest.setAttribute("email", claims.get("email"));
//            httpRequest.setAttribute("userRole", claims.get("userRole"));
//            httpRequest.setAttribute("nickname", nickname);  // 닉네임 설정(setAttribute)
//
//            // 관리자 권한 인가 처리
//            if (url.startsWith("/admin")) {
//                // 관리자 권한이 없는 경우 403을 반환합니다.
//                if (!UserRole.ADMIN.equals(userRole)) {
//                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
//                    return;
//                }
//                chain.doFilter(request, response);
//                return;
//            }
//
//            // 토큰이 유효하고 권한이 적절하면 다음 필터로 요청 전달
//            chain.doFilter(request, response);
//        } catch (SecurityException | MalformedJwtException e) {
//            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
//            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
//        } catch (ExpiredJwtException e) {
//            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
//            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
//        } catch (UnsupportedJwtException e) {
//            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
//            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
//        } catch (Exception e) {
//            log.error("Internal server error", e);
//            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}
