package com.springboot.club_house_api_server.game.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GameStartDto {
    private Long gameId;
    private List<Long> userIdList;
}
