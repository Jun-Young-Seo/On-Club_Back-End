package com.springboot.club_house_api_server.membership.dto;

import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipResponseDto {
    private Long userId;
    private Long membershipId;
    private int attendanceCount;
    private int eventCount;
    private LocalDateTime joinedAt;
    private MembershipEntity.RoleType role;
    private String userName;
    private String userTel;
    private UserEntity.Gender gender;
    private int career;
    private LocalDate birthDate;
    private String region;
}
