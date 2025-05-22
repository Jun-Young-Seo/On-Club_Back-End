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

    @Override
    public String toString() {
        return "MemberChartDataDto{" +
                "howManyMembers=" + howManyMembers +
                ", howManyMembersBetweenOneMonth=" + howManyMembersBetweenOneMonth +
                ", howManyAccumulatedGuests=" + howManyAccumulatedGuests +
                ", howManyGuestsBetweenOneMonth=" + howManyGuestsBetweenOneMonth +
                ", howManyEventsBetweenOneMonth=" + howManyEventsBetweenOneMonth +
                ", attendanceCount=" + attendanceCount +
                ", maleMembers=" + maleMembers +
                ", femaleMembers=" + femaleMembers +
                ", mostAttendantMember=" + (mostAttendantMember != null ? mostAttendantMember.getUserName() : "most attendant user null") +
                ", mostManyGamesMember=" + (mostManyGamesMember != null ? mostManyGamesMember.getUserName() : "most many game user null") +
                '}';
    }
}
