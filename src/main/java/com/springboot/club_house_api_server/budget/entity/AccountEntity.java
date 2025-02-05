package com.springboot.club_house_api_server.budget.entity;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "club_account")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name="account_owner")
    private String accountOwner;

    @Column(name = "bank_name")
    private String bankName;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions;
}
