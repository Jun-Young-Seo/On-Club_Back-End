package com.springboot.club_house_api_server.budget.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BudgetResponseDto {
    private long transactionId;
    private long transactionAmount;
    private long transactionBalance;
    private String transactionCategory;
    private LocalDateTime transactionDate;
    private String transactionDescription;
    private String transactionDetail;
    private String transactionMemo;
    private String transactionType;
}
