package com.springboot.club_house_api_server.guest.repository;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.guest.entity.GuestEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<GuestEntity, Long> {

    long countByClub_ClubId(Long clubId);

    @Query("SELECT g FROM GuestEntity g WHERE g.user = :user AND g.event = :event")
    Optional<GuestEntity> findByUserAndEvent(@Param("user") UserEntity user, @Param("event") ClubEventEntity event);

    @Query("SELECT g.event.eventId FROM GuestEntity g where g.user.userId = :userId")
    List<Long> findAllGuestEventsByUserId(@Param("userId") Long userId);

    @Query("SELECT g.user.userId FROM GuestEntity g where g.event.eventId = :eventId")
    List<Long> findAllGuestUserIdByEventId(@Param("eventId") Long eventId);

    @Query("SELECT g FROM GuestEntity g WHERE g.user.userId=:userId")
    List<GuestEntity> findAllGuestEntitiesByUserId(@Param("userId") Long userId);

    //이달에 참석한 게스트 ID 가져오기
    //보고서 작성용
    @Query("""
    SELECT DISTINCT g.user
    FROM GuestEntity g
    WHERE g.event.eventStartTime BETWEEN :startDate AND :endDate
    """)
    List<UserEntity> findAttendedGuestUserIds(@Param("startDate") LocalDateTime start,
                                        @Param("endDate") LocalDateTime end);


}
