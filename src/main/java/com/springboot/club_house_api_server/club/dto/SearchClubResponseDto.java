package com.springboot.club_house_api_server.club.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter

public class SearchClubResponseDto {
    private long club_id;
    private String clubName;
    private String clubDescription;
    private String clubDescriptionDetail;
    private String clubLogoURL;
    private String clubBackgroundImageURL;
    private LocalDateTime clubWhenCreated;
    private Long clubMemberCount;
    private Long guestCount;
    private String tagOne;
    private String tagTwo;
    private String tagThree;
}
