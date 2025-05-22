package com.springboot.club_house_api_server.membership.repository;

import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity,Long> {

    @Query("SELECT  m from MembershipEntity  m WHERE m.membershipId=:membershipId")
    Optional<MembershipEntity> findByMembershipId(@Param("membershipId") Long membershipId);

    @Query("SELECT m FROM MembershipEntity m WHERE m.user.userId = :userId AND m.club.clubId = :clubId")
    Optional<MembershipEntity> getMembershipEntityByUserIdAndClubId(@Param("userId") Long userId, @Param("clubId") Long clubId);

    @Query("SELECT m FROM MembershipEntity m WHERE m.user.userId = :userId")
    List<MembershipEntity> findAllMembershipsByUserId(@Param("userId") Long userId);

    @Query("SELECT m from MembershipEntity m WHERE m.club.clubId=:clubId")
    List<MembershipEntity> findAllMembershipsByClubId(@Param("clubId") Long clubId);

    @Query("SELECT m.user.userId FROM MembershipEntity m WHERE m.club.clubId = :clubId AND (m.role = 'MANAGER' OR m.role = 'LEADER')")
    List<Long> findUserIdsOfManagersAndLeadersByClubId(@Param("clubId") Long clubId);

    @Query("SELECT m.user.userId FROM MembershipEntity  m WHERE m.club.clubId = :clubId")
    List<Long> findUserIdsOfAllRolesByClubId(@Param("clubId") Long clubId);

    @Query("SELECT COUNT(m) FROM MembershipEntity m WHERE m.club.clubId = :clubId")
    Long countAllMemberships(@Param("clubId")Long clubId);

    @Query("""
    SELECT COUNT(m)
    FROM MembershipEntity m
    WHERE m.club.clubId = :clubId
      AND m.joinedAt BETWEEN :startDate AND :endDate
    """)
        Long countMembershipsJoinedBetween(
                @Param("clubId") Long clubId,
                @Param("startDate") LocalDateTime startDate,
                @Param("endDate") LocalDateTime endDate
        );

    @Query("""
    SELECT COUNT(m)
    FROM MembershipEntity m
    WHERE m.club.clubId = :clubId and
        m.user.gender = :gender
    """)
        Integer countMembershipsWithGender(
            @Param("clubId") Long clubId,
            @Param("gender") UserEntity.Gender gender
        );

}
