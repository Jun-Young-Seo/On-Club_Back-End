package com.springboot.club_house_api_server.config.security;

import com.springboot.club_house_api_server.jwt.filter.JwtAuthenticationFilter;
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
                //authorize.antMatchers("/api/admin/**").hasRole("ADMIN")
                //         .antMatchers("/api/user/**").authenticated();
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                //모든 요청을 가로채서 jwtAuthenticationFilter를 거치게 하기
                //UsernamePasswordAuthenticationFilter(Spring Security 기본필터)전에 실행됨
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenGenerator),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoding
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
