package com.springboot.club_house_api_server.report.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

@Getter
@Setter
public class GameStatDto {
    private String userName;
    private String userTel;
    private UserEntity.Gender gender;
    private Integer career;
    private Long totalGames;
    private Long totalScore;
    private Long attendanceCount;

    public GameStatDto(String userName, String userTel, UserEntity.Gender gender, Integer career,
                       Long totalGames, Long totalScore, Long attendanceCount) {
        this.userName = userName;
        this.userTel = userTel;
        this.gender = gender;
        this.career = career;
        this.totalGames = totalGames;
        this.totalScore = totalScore;
        this.attendanceCount = attendanceCount;
    }

}
