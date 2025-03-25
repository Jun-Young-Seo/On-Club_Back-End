package com.springboot.club_house_api_server.budget.dto;

import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder

public class AddNewTransactionDto {
    private long clubAccountId;
    private long clubId;
    private LocalDateTime transactionDate;
    private String transactionType;
    private int transactionAmount;
    private int transactionBalance;
    private String transactionCategory;
    private String transactionDescription;
    private String transactionMemo;
    private String transactionDetail;
}
