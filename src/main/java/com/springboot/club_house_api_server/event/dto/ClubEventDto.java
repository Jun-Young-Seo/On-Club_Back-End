package com.springboot.club_house_api_server.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClubEventDto {
    private long eventId;
    private long clubId;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private String eventDescription;
}
