package com.springboot.club_house_api_server.budget.repository;

import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<ClubAccountEntity,Long> {
    List<ClubAccountEntity> findByAccountName(String accountName);

    @Query("SELECT account FROM ClubAccountEntity account WHERE account.club.clubId = :clubId")
    List<ClubAccountEntity> findAccountsByClubId(@Param("clubId") Long clubId);

    Optional<ClubAccountEntity> findByAccountNumber(String accountNumber);

    @Query("SELECT account from  ClubAccountEntity account WHERE account.club.clubName= :clubName")
    List<ClubAccountEntity> findByClubName(String clubName);

}
