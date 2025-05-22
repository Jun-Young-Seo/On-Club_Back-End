package com.springboot.club_house_api_server.report.dto;

import com.springboot.club_house_api_server.user.dto.UserInfoDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberChartDataDto {
    private Integer howManyMembers;
    private Long howManyMembersBetweenOneMonth;
    private Integer howManyAccumulatedGuests;
    private Integer howManyGuestsBetweenOneMonth;
    private Long howManyEventsBetweenOneMonth;
    private Integer attendanceCount;
    private Integer maleMembers;
    private Integer femaleMembers;
//    private Double averageAttendanceRate;
    private UserInfoDto mostAttendantMember;
//    private UserEntity mostWinnerMember;
    private UserInfoDto mostManyGamesMember;

}
