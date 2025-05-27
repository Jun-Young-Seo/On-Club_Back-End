package com.springboot.club_house_api_server.game.dto;

import com.springboot.club_house_api_server.game.entity.TeamEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder

public class GetGameDto {
    private Long gameId;
    private List<String> userNames;
    private LocalDateTime joinedAt;
    private LocalDateTime finishedAt;
    private Integer teamOneScore;
    private Integer teamTwoScore;
    private Long teamOneId;
    private Long teamTwoId;
}
