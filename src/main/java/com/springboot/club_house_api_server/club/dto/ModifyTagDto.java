package com.springboot.club_house_api_server.club.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyTagDto {
    long clubId;
    private String tagOne;
    private String tagTwo;
    private String tagThree;
}
