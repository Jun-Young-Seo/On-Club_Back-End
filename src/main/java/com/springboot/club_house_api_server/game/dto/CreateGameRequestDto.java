package com.springboot.club_house_api_server.game.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGameRequestDto {
    private List<Long> userIdList;
    private Long eventId;
    private Long clubId;
    private LocalDateTime matchStartTime;
    private LocalDateTime lastGamedAt;
}
