package com.springboot.club_house_api_server.game.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class EndGameDto {
    private Long gameId;
    private Long teamOneId;
    private Integer teamOneScore;
    private Long teamTwoId;
    private Integer teamTwoScore;
}
