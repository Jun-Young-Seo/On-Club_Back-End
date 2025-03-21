package com.springboot.club_house_api_server.jwt.filter;

import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 추출
        // SSL 이슈가 해결되면 쿠키에서 추출하는걸로 바꾸기
        String token = resolveToken(request);

        // 토큰이 존재하고 유효하면 인증 정보 설정
        if (token != null && jwtTokenGenerator.validateToken(token)) {
            Authentication authentication = jwtTokenGenerator.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        //Cookie 방식은 SSL이 필용해서 일단 보류...
//
//        //1차 확인 -> 쿠키에서
//        if(request.getCookies() != null) {
//
//            for (Cookie cookie : request.getCookies()) {
//                System.out.println(cookie.getName());
//                if(cookie.getName().equals("accessToken")){
//                    return cookie.getValue();
//                }
//            }
//        }
//      // Authorization 헤더에서 Bearer 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
        }
        return null;
    }
}
