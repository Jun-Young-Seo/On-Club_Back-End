package com.springboot.club_house_api_server.game.dto;


import com.springboot.club_house_api_server.game.entity.GameEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponseDto {
    private Long gameId;
    private Long eventId;
    private Long clubId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private GameEntity.GameStatus status;
    private List<Long> userIdList;
    private Long teamOneId;
    private Long teamTwoId;
}
