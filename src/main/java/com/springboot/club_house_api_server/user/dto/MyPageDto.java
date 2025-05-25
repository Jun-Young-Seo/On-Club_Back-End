package com.springboot.club_house_api_server.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MyPageDto {
    //클럽 가입 수
    long countMemberships;
    //이번달 참여 횟수
    long countParticipantThisMonth;
    //누적참여횟수
    long countAccumulateParticipant;
}
