package com.springboot.club_house_api_server.membership.dto;

import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MembershipJoinDto {
    private long userId;
    private long clubId;
    private MembershipEntity.RoleType role;
}
