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
    //계좌 이름으로 클럽 조회
    List<ClubAccountEntity> findByAccountName(String accountName);

    //클럽 ID로 계좌 조회
    @Query("SELECT account FROM ClubAccountEntity account WHERE account.club.clubId = :clubId")
    List<ClubAccountEntity> findAccountsByClubId(@Param("clubId") Long clubId);

    //계좌번호로 클럽 조회
    Optional<ClubAccountEntity> findByAccountNumber(String accountNumber);

    //클럽 이름으로 계좌 조회
    @Query("SELECT account from  ClubAccountEntity account WHERE account.club.clubName= :clubName")
    List<ClubAccountEntity> findByClubName(String clubName);

}
