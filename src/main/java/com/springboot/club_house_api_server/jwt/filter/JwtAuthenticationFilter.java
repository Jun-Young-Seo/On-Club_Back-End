package com.springboot.club_house_api_server.jwt.filter;


import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
//GenericFilterBean은 SpringSecurity의 필터 클래스
//상속받아 doFilter를 구현해서 커스텀 필터 만들어 사용
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenGenerator jwtTokenGenerator;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //JWT 추출
        String token = resolveToken((HttpServletRequest) servletRequest);
        //token 정보가 있고, SIgn 검증이 되면
        if(token != null && jwtTokenGenerator.validateToken(token)) {
            //Spring Security의 Authentication 객체 생성
            Authentication authentication = jwtTokenGenerator.getAuthentication(token);
            //생성된 정보를 Spring Security contextHolder로 관리
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //다음 필터로 요청 전달하기
        //이거 없으면 컨트롤러까지 요청 안감. JWT 커스텀 필터로 요청 가로챘으니까 원래 필터로 돌려줘야됨.
        filterChain.doFilter(servletRequest,servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        //헤더에서 JWT 꺼내기
        String bearerToken = request.getHeader("Authorization");
        //Bearer 뒤가 진짜 JWT내용
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
