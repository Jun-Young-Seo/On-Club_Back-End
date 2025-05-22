package com.springboot.club_house_api_server.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MembershipReportDto {
    private String month;
    private int memberCount;
    private int attendanceCount;

}
