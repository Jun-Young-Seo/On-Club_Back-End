package com.springboot.club_house_api_server.guest.repository;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.guest.entity.GuestEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<GuestEntity, Long> {

    @Query("SELECT g FROM GuestEntity g WHERE g.user = :user AND g.event = :event")
    Optional<GuestEntity> findByUserAndEvent(@Param("user") UserEntity user, @Param("event") ClubEventEntity event);

    @Query("SELECT g.event.eventId FROM GuestEntity g where g.user.userId = :userId")
    List<Long> findAllGuestEventsByUserId(@Param("userId") Long userId);
}
