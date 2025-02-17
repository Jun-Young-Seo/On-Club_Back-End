package com.springboot.club_house_api_server.event.repository;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClubEventRepository extends JpaRepository<ClubEventEntity,Long> {

    //클럽 ID로 그 클럽의 모든 이벤트 조회
    @Query("select e from ClubEventEntity e WHERE e.club.clubId = :clubId")
    List<ClubEventEntity> findAllEventsByClubId(@Param("clubId") long clubId);

    //클럽 ID와 시간으로 특정 시간대의 모든 이벤트 조회
    @Query("SELECT e FROM ClubEventEntity e WHERE e.eventStartTime BETWEEN :startTime AND :endTime AND e.club.clubId = :clubId")
    List<ClubEventEntity> findAllEventsByClubIdAndTimeRange(
            @Param("clubId") long clubId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    //특정 클럽 + 특정 시간 + 설명에 키워드가 포함된 모든 이벤트 조회하기
    @Query("SELECT e FROM ClubEventEntity e " +
            "JOIN e.club c " +
            "WHERE c.clubId = :clubId " +
            "AND e.eventStartTime BETWEEN :startTime AND :endTime " +
            "AND e.eventDescription LIKE %:keyword%")
    List<ClubEventEntity> findAllEventsByClubIdAndDateAndKeyword(
            @Param("clubId") long clubId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("keyword") String keyword
    );


}
