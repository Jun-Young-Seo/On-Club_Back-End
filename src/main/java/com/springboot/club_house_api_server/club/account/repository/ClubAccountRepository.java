package com.springboot.club_house_api_server.club.account.repository;

import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClubAccountRepository extends JpaRepository<ClubAccountEntity, Long> {

    @Query("SELECT COUNT(a) > 0 FROM ClubAccountEntity a WHERE a.club = :club AND a.accountName = :accountName")
    boolean isAlreadyExistAccount(@Param("club") ClubEntity club, @Param("accountName") String accountName);

}
