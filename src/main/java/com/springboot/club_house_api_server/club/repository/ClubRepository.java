package com.springboot.club_house_api_server.club.repository;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Long> {

    //랜덤ㅋ ㅡㄹ럽찾기
    @Query("SELECT c FROM ClubEntity c ORDER BY FUNCTION('RAND')")
    List<ClubEntity> findRandomClubs();

    // 1. 클럽 이름으로 클럽 조회
    @Query("SELECT c FROM ClubEntity c WHERE c.clubName = :name")
    Optional<ClubEntity> findByClubName(@Param("name") String name);

//    // 이미지 검색용 -- 쓸까?
//    @Query("SELECT c FROM ClubEntity c WHERE c.clubLogoURL = :clubImageURL")
//    Optional<ClubEntity> findByClubImageURL(@Param("clubImageURL") String clubImageURL);

    // 2. 특정 기간 동안 생성된 클럽 조회 (클럽 생성일 기준)
    @Query("SELECT c FROM ClubEntity c WHERE c.clubCreatedAt BETWEEN :startDate AND :endDate")
    List<ClubEntity> findClubsCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    // 3. 특정 클럽에 가입한 모든 User 조회
    @Query("SELECT u FROM UserEntity u JOIN u.memberships m WHERE m.club.clubId = :clubId")
    List<UserEntity> findAllClubMembers(@Param("clubId") Long clubId);

    // 4. 특정 클럽에 특정 기간 동안 가입한 사용자 조회 (Membership 조인)
    @Query("SELECT u FROM UserEntity u JOIN u.memberships m WHERE m.club.clubId = :clubId AND m.joinedAt BETWEEN :startDate AND :endDate")
    List<UserEntity> findMembersByClubIdAndJoinDateBetween(@Param("clubId") Long clubId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);

    // 5. 특정 클럽의 가입자 수 조회
    @Query("SELECT COUNT(m) FROM MembershipEntity m WHERE m.club.clubId = :clubId")
    Long countMembersByClubId(@Param("clubId") Long clubId);

    // 6. 특정 클럽에 특정 역할(ROLE)을 가진 사용자 수 조회
    @Query("SELECT COUNT(m) FROM MembershipEntity m WHERE m.club.clubId = :clubId AND m.role = :role")
    Long countMembersByClubIdAndRole(@Param("clubId") Long clubId, @Param("role") String role);

    //User가 가입 한 클럽 -- membership Join
    @Query("SELECT c FROM ClubEntity c JOIN c.memberships m WHERE m.user.userId = :userId ORDER BY c.clubName ASC")
    List<ClubEntity> findClubsByUserId(@Param("userId") Long userId);

    @Query("SELECT c.clubTagOne, c.clubTagTwo, c.clubTagThree FROM ClubEntity c WHERE c.clubId = :clubId")
    List<String> findTagsByClubId(@Param("clubId") Long clubId);
}
