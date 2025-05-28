package com.springboot.club_house_api_server.report.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AttendanceStatDto {
    private final String userName;
    private final String userTel;
    private final UserEntity.Gender gender;
    private final Integer career;
    private final Long attendanceCount;

    public AttendanceStatDto(String userName, String userTel, UserEntity.Gender gender, Integer career, Long attendanceCount) {
        this.userName = userName;
        this.userTel = userTel;
        this.gender = gender;
        this.career = career;
        this.attendanceCount = attendanceCount;
    }
}
