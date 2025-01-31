package com.springboot.club_house_api_server.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JoinRequestDto {
    private String userName;
    private String userTel;
    private String password;
}

