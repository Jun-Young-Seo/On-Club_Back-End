package com.springboot.club_house_api_server.membership.repository;

import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity,Long> {
    @Query("SELECT m FROM MembershipEntity m WHERE m.user.userId = :userId AND m.club.clubId = :clubId")
    Optional<MembershipEntity> checkAlreadyJoined(@Param("userId") Long userId, @Param("clubId") Long clubId);

}
