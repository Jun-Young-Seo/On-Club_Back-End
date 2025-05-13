package com.springboot.club_house_api_server.openai.analyze.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClubDescriptionDto {
    private String clubName;
    private String region;
    private String careerRange;
    private String purpose;
}
