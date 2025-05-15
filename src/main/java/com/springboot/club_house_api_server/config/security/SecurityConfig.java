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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenGenerator jwtTokenGenerator;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // CORS 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // HTTP 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // Spring 기본 로그인 방식 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 방식이 아니므로 STATELESS
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 모든 요청 허용
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/user/login", "/api/user/join", "/api/user/logout","/api/user/refresh"
                                ,"/api/club/add","/api/club/find/**", "/api/event/get-event/**", "/api/guest/attend/request",
                                "/api/membership/join/request", "/api/membership/my-role"
                                ,"/api/membership/withdraw","/api/notification/**", "/api/user/info",
                                "/api/membership/join/direct-user","/api/membership/join/direct-not-user", "/api/s3/upload-file"
                        ,"/api/participant/**", "/api/openai/**").permitAll()
                        .requestMatchers("/api/budget/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/chart/**").hasAnyRole("MANAGER","LEADER")
                        .requestMatchers("/club/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/account/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/event/add-event").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/excel/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/game/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/membership/**").hasAnyRole("LEADER","MANAGER")
//                        .requestMatchers("/api/openai/**").hasAnyRole("LEADER","MANAGER")
//                        .requestMatchers("/api/participant/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/s3/**").hasAnyRole("LEADER","MANAGER")
                        .requestMatchers("/api/guest/**").hasAnyRole("LEADER","MANAGER")
                        .anyRequest().authenticated()
                )
                // 커스텀 예외 필터 -> 인증 필터로 처리
                // 커스텀 예외 필터에서 만료 토큰, 위조 토큰 처리
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

    //  최신 CORS 설정 (Spring Security 6.1 이상 방식)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://on-club.co.kr",
                "https://www.on-club.co.kr"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
