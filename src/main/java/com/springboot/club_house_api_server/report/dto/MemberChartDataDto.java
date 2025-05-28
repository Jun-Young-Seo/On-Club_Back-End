package com.springboot.club_house_api_server.report.dto;

import com.springboot.club_house_api_server.user.dto.UserInfoDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberChartDataDto {
    private int year;
    private int month;

    private Integer howManyMembers;
    private Long howManyMembersBetweenOneMonth;
    private Integer howManyAccumulatedGuests;
    private Integer howManyGuestsBetweenOneMonth;
    private Long howManyEventsBetweenOneMonth;
    private Integer attendanceCount;
    private Integer maleMembers;
    private Integer femaleMembers;
    private Double averageAttendanceRate;
    private List<GameStatDto> mostAttendantMember;
    private List<GameStatDto> mostWinnerMember;
    private List<GameStatDto> mostManyGamesMember;

}
