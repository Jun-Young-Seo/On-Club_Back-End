package com.springboot.club_house_api_server.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:3000")  // React 프론트엔드 허용
                        .allowedOrigins("http://localhost:3000", "https://on-club.co.kr")
                        .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS", "PATCH")  // 허용할 HTTP 메서드
                        .allowedHeaders("Authorization","Content-Type")
                        .allowCredentials(true);  // 쿠키 및 인증 정보 포함 허용
            }
        };
    }
}
