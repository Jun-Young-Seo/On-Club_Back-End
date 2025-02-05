package com.springboot.club_house_api_server.budget.repository;

import com.springboot.club_house_api_server.budget.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity,Long> {
    List<AccountEntity> findByAccountName(String accountName);

    @Query("SELECT account FROM AccountEntity account WHERE account.club.clubId = :clubId")
    List<AccountEntity> findAccountsByClubId(@Param("clubId") Long clubId);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    @Query("SELECT account from  AccountEntity account WHERE account.club.clubName= :clubName")
    List<AccountEntity> findByClubName(String clubName);

}
