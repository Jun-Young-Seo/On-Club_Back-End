package com.springboot.club_house_api_server.game.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class GamePlayStatDto {
    private final String userName;
    private final String userTel;
    private final UserEntity.Gender gender;
    private final Integer career;
    private final Long totalGames;

    public GamePlayStatDto(String userName, String userTel, UserEntity.Gender gender, Integer career, Long totalGames) {
        this.userName = userName;
        this.userTel = userTel;
        this.gender = gender;
        this.career = career;
        this.totalGames = totalGames;
    }
}
