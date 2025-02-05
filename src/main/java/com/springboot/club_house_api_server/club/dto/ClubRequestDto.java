package com.springboot.club_house_api_server.club.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ClubRequestDto {
    private String clubName;
    private String clubDescription;
    private String clubLogoURL;
    private String clubBackgroundURL;
}
