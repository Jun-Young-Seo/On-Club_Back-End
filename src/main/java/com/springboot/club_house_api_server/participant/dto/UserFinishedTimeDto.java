package com.springboot.club_house_api_server.participant.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFinishedTimeDto {
    private Long userId;
    private String userName;
    private LocalDateTime lastGamedAt;
    private Integer career;
    private UserEntity.Gender gender;
    private Long gameCount;

//    public UserFinishedTimeDto(Long userId, LocalDateTime finishedTime) {
//        this.userId = userId;
//        this.finishedTime = finishedTime;
//    }
}
