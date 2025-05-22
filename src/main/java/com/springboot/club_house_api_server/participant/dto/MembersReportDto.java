package com.springboot.club_house_api_server.participant.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembersReportDto {
    private UserEntity user;
    private Long attendanceCount;
}
