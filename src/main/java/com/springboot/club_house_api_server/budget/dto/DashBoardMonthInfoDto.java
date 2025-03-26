package com.springboot.club_house_api_server.budget.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashBoardMonthInfoDto {
    private long balance;
    private long monthlyExpense;
    private long monthlyIncome;
    private long monthlySurplus;
}
