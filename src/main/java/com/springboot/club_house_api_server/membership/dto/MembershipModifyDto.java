package com.springboot.club_house_api_server.membership.dto;

import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter

public class MembershipModifyDto {
    private long membershipId;
    private MembershipEntity.RoleType role;
}
