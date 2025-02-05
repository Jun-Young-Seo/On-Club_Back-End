package com.springboot.club_house_api_server.user.repository;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 1. 사용자 전화번호로 사용자 조회 (로그인 시 사용)
    @Query("SELECT u FROM UserEntity u WHERE u.userTel = :userTel")
    Optional<UserEntity> findByUserTel(@Param("userTel") String userTel);

    // 2. 사용자 이름으로 사용자 조회
    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName")
    List<UserEntity> findByUserName(@Param("userName") String userName);

    // 3. 특정 클럽에 가입한 사용자 조회 (Membership 테이블 조인)
    @Query("SELECT u FROM UserEntity u JOIN u.memberships m WHERE m.club.clubId = :clubId")
    List<UserEntity> findUsersByClubId(@Param("clubId") Long clubId);

    // 4. 특정 클럽에 가입한 사용자 중 특정 역할(ROLE)을 가진 사용자 조회
    @Query("SELECT u FROM UserEntity u JOIN u.memberships m WHERE m.club.clubId = :clubId AND m.role = :role")
    List<UserEntity> findUsersByClubIdAndRole(@Param("clubId") Long clubId, @Param("role") String role);

    // 5. JWT 리프레시 토큰으로 사용자 조회 (토큰 갱신 시 사용)
    @Query("SELECT u FROM UserEntity u WHERE u.refreshToken = :refreshToken")
    Optional<UserEntity> findByRefreshToken(@Param("refreshToken") String refreshToken);
}
