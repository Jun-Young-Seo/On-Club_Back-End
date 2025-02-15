package com.springboot.club_house_api_server.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String userTel;
    private String password;
}
