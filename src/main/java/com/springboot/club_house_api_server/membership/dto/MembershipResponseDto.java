package com.springboot.club_house_api_server.membership.dto;

import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipResponseDto {
    private Long membershipId;
    private double attendanceRate;
    private MembershipEntity.RoleType role;
    private String userName;
    private String userTel;
    private UserEntity.Gender gender;
    private int career;
    private LocalDate birthDate;
    private String region;
}
