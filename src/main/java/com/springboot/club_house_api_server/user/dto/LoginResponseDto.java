package com.springboot.club_house_api_server.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
//로그인 응답용 DTO
public class LoginResponseDto {
    private long userId;
    private String accessToken;
    private String refreshToken;
}
