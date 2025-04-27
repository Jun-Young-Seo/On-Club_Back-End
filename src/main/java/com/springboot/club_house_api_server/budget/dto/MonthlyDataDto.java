package com.springboot.club_house_api_server.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@AllArgsConstructor
@Builder
@Getter
@Setter

public class MonthlyDataDto {
    private YearMonth yearMonth;
    private Long income;
    private Long expense;
}

