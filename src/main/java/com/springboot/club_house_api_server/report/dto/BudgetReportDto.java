package com.springboot.club_house_api_server.report.dto;

import com.springboot.club_house_api_server.budget.dto.CategorySummaryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class BudgetReportDto {
    private String month;
    private Long totalIncome;
    private Long totalExpense;
    private Long netProfit;
    private Long membershipFee;
    private int memberCount;
    private int feePerMember;
    private List<CategorySummaryDto> categorySummary;
}
