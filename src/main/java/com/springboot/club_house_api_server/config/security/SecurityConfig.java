package com.springboot.club_house_api_server.config.security;

import com.springboot.club_house_api_server.jwt.filter.JwtAuthenticationFilter;
import com.springboot.club_house_api_server.jwt.filter.JwtExceptionFilter;
import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenGenerator jwtTokenGenerator;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //HTTP 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                //CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                //SPring 기본 로그인 방식 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                //세션 방식 아니니까 STATELESS
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //모든 요청 허용 --> 테스트라서.
                //login Endpoint 만들면 수정하기
                //ex)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/user/login","/api/user/join", "/api/user/logout").permitAll()  // ROLE_ 접두사는 자동으로 붙여짐
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .anyRequest().permitAll()
                )
                //커스텀 예외 필터 -> 인증 필터로 처리
                //커스텀 예외 필터에서 만료토큰, 위조토큰 처리
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenGenerator),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoding
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
