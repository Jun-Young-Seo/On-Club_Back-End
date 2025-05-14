package com.springboot.club_house_api_server.participant.repository;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {


    @Query("SELECT p FROM ParticipantEntity p WHERE p.event.eventId = :eventId")
    List<ParticipantEntity> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT p FROM ParticipantEntity p WHERE p.user.userId = :userId AND p.event.eventId = :eventId")
    Optional<ParticipantEntity> findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT p.event FROM ParticipantEntity p WHERE p.user.userId = :userId")
    List<ClubEventEntity> findAllEventIdsByUserId(@Param("userId") Long userId);
}
