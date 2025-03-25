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
    private int transactionAmount;
    private int transactionBalance;
    private String transactionCategory;
    private LocalDateTime transactionDate;
    private String transactionDescription;
    private String transactionDetail;
    private String transactionMemo;
    private String transactionType;
}
