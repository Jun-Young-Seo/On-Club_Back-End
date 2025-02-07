package com.springboot.club_house_api_server.club.account.entity;

import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@RequiredArgsConstructor
@Table(name = "club_account")
public class ClubAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @Column(name = "account_name")
    private String accountName;

    @Column(name="account_number")
    private String accountNumber;

    @Column(name = "account_owner")
    private String accountOwner;

    @Column(name = "bank_name")
    private String bankName;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions;

    public ClubAccountEntity(ClubEntity club, String accountName,
                             String accountNumber, String accountOwner, String bankName) {
        this.club = club;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountOwner = accountOwner;
        this.bankName = bankName;
    }
}