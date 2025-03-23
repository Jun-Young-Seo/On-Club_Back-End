package com.springboot.club_house_api_server.club.account.repository;

import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClubAccountRepository extends JpaRepository<ClubAccountEntity, Long> {

    //ㅇ이미 있는 계좌인지 검증
    @Query("SELECT COUNT(a) > 0 FROM ClubAccountEntity a WHERE a.club = :club AND a.accountName = :accountName")
    boolean isAlreadyExistAccount(@Param("club") ClubEntity club, @Param("accountName") String accountName);

    //ClubID에 해당되는 모든 계좌 엔티티 반환
    @Query("SELECT a FROM ClubAccountEntity a WHERE a.club.clubId = :clubId")
    List<ClubAccountEntity> findAllAccountsByClubId(@Param("clubId") Long clubId);


}
