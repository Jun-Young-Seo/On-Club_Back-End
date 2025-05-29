package com.springboot.club_house_api_server.jwt.filter;

import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/user/login") ||
                path.startsWith("/api/user/join") ||
                path.startsWith("/api/user/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String token = resolveToken(request);
            if(token==null){
                handleJwtError(response, "Expired", "accessToken이 없거나 만료되었습니다.");
                return;
            }
            // validateToken 내부에서 MalformedJwtException 발생 가능
            try {
                if (jwtTokenGenerator.validateToken(token)) {
                    Authentication authentication = jwtTokenGenerator.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (MalformedJwtException e) {
                // 여기서 바로 응답 처리
                // 원래는 전역 핸들러를 잡아야 맞다지만, 귀찮다...
                handleJwtError(response, "Malformed", "잘못된 JWT 형식입니다.");
                return; // 필터 체인 진행 중단
            } catch (ExpiredJwtException e) {
                handleJwtError(response, "Expired", "토큰이 만료되었습니다.");
                return;
            } catch (JwtException e) {
                handleJwtError(response, "Invalid", "유효하지 않은 토큰입니다.");
                return;
            }

            filterChain.doFilter(request, response); // 다음 필터로 진행

        } catch (Exception e) {
            throw e; // 이건 내부 서버 에러 등 전역 처리용
        }
    }

    private String resolveToken(HttpServletRequest request) {
//      // Authorization 헤더에서 Bearer 토큰 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
//
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
//        }
//
}
    private void handleJwtError(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String body = String.format("""
        {
          "error": "%s",
          "message": "%s",
          "timestamp": "%s"
        }
        """, error, message, java.time.LocalDateTime.now());

        response.getWriter().write(body);
    }

}
