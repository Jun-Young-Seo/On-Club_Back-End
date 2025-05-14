package com.springboot.club_house_api_server.participant.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

@Getter
@Setter

public class FindAllEventsDto {
    private Long userId;
    private Long eventId;
    private String clubName;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private String eventDescription;
}
