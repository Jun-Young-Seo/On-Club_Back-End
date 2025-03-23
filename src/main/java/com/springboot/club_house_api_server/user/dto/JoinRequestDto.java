package com.springboot.club_house_api_server.user.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class JoinRequestDto {
    private String userName;
    private String userTel;
    private String password;
    private String region;
    private UserEntity.Gender gender;
    private LocalDate birthDate;
    private int career;
}

