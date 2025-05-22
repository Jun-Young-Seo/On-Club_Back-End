package com.springboot.club_house_api_server.budget.entity;


import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@Table(name="transaction")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id", nullable=false)
    private ClubAccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id",nullable = false)
    private ClubEntity club;

    @Column(name="transaction_date", nullable=false)
    private LocalDateTime transactionDate;

    @Column(name="transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "transaction_amount",nullable = false)
    private int transactionAmount;

    @Column(name = "transaction_balance", nullable = false)
    private int transactionBalance;

    @Column(name="transaction_category",nullable = false)
    private String transactionCategory;

    //여기 부분이 받는 분 통장에 표시할 내용에 해당
    @Column(name="transaction_description", nullable = false)
    private String transactionDescription;

    //카카오뱅크 자체 기능. 거래 내역에 메모를 남기면 있는 필드
    @Column(name="transaction_memo")
    private String transactionMemo;

    //서비스 내에서 거래를 구분해서 추가할 필드. 교통비 간식비 등등
    @Column(name="transaction_detail")
    private String transactionDetail;

    //서비스 계층 호출용 생성자
    public TransactionEntity(ClubAccountEntity account, ClubEntity club, LocalDateTime transactionDate,
                             String transactionType, int transactionAmount, int transactionBalance,
                             String transactionCategory, String transactionDescription,
                             String transactionMemo, String transactionDetail) {
        this.account = account;
        this.club = club;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionBalance = transactionBalance;
        this.transactionCategory = transactionCategory;
        this.transactionDescription = transactionDescription;
        this.transactionMemo = transactionMemo;
        this.transactionDetail=transactionDetail;
    }

}
