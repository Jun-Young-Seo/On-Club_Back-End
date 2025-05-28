package com.springboot.club_house_api_server.game.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ScoreStatDto {
    private final String userName;
    private final String userTel;
    private final UserEntity.Gender gender;
    private final Integer career;
    private final Long totalScore;

    public ScoreStatDto(String userName, String userTel, UserEntity.Gender gender, Integer career, Long totalScore) {
        this.userName = userName;
        this.userTel = userTel;
        this.gender = gender;
        this.career = career;
        this.totalScore = totalScore;
    }
}
