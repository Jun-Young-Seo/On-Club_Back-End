package com.springboot.club_house_api_server.budget.entity;


import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@Table(name="transaction")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id", nullable=false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id",nullable = false)
    private ClubEntity club;

    @Column(name="transaction_date", nullable=false)
    private LocalDate transactionDate;

    @Column(name="transaction_description", nullable = false)
    private String transactionDescription;

    @Column(name="transaction_category",nullable = false)
    private String transactionCategory;

    @Column(name="transaction_who",nullable = false)
    private String transactionWho;

    @Column(name="transaction_detail",nullable = false)
    private String transactionDetail;
}
