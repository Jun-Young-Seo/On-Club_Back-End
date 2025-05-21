package com.springboot.club_house_api_server.participant.dto;


import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResponseDto {
    private long userId;
    private String userName;
    private UserEntity.Gender gender;
//    private LocalDateTime lastGamedAt;
    private int career;
    private int gameCount;
}
