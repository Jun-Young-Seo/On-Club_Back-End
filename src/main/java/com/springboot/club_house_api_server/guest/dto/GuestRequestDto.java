package com.springboot.club_house_api_server.guest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestRequestDto {
    private long eventId;
    private long userId;
}
