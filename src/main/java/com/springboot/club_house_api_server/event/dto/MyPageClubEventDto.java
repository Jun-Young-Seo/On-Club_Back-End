package com.springboot.club_house_api_server.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder

public class MyPageClubEventDto {
    private String clubName;
    private String eventDescription;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private String type;
}
