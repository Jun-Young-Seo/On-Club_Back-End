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
    private List<Long> userIdListForTeamOne;
    private List<Long> userIdListForTeamTwo;
    private Long eventId;
    private Long clubId;
    private LocalDateTime matchStartTime;
    private LocalDateTime lastGamedAt;

    @Override
    public String toString() {
        return "userIdListForTeamOne : " + userIdListForTeamOne.get(0)+", " + userIdListForTeamOne.get(1)+
                "\n" + "userIdListForTeam Two : " + userIdListForTeamTwo.get(0) +", "+ userIdListForTeamTwo.get(1)+
                "eventId : "+eventId+"clubId : "+clubId;
    }
}
