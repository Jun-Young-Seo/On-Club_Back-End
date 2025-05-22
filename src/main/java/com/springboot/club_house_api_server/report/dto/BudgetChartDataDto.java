package com.springboot.club_house_api_server.report.dto;


import com.springboot.club_house_api_server.budget.dto.CategorySummaryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BudgetChartDataDto {
    private Long totalIncome;
    private Long totalExpense;
    List<CategorySummaryDto> categorySummaries;
}
