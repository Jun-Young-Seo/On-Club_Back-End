package com.springboot.club_house_api_server.participant.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ParticipantGuestDto {
    private long userId;
    private String userName;
    private UserEntity.Gender gender;
//    private LocalDateTime lastGamedAt;
    private int career;
    private int gameCount;
    private LocalDate birthDate;
    private String region;
    private String userTel;
}
