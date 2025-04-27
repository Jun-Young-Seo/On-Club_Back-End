package com.springboot.club_house_api_server.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExpenseSummaryDto {
    private String transactionDetail;
    private Long totalAmount;
}
