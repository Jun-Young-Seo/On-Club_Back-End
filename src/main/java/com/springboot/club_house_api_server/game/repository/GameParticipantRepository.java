package com.springboot.club_house_api_server.game.repository;

import com.springboot.club_house_api_server.game.entity.GameParticipantEntity;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameParticipantRepository extends JpaRepository<GameParticipantEntity, Long> {

    @Query("SELECT gp FROM GameParticipantEntity gp WHERE gp.game.gameId = :gameId")
    List<GameParticipantEntity> findByGameId(@Param("gameId") Long gameId);

    @Query("SELECT gp.user.userId FROM GameParticipantEntity gp WHERE gp.game.gameId = :gameId")
    List<Long> findUserIdsByGameId(@Param("gameId") Long gameId);

    @Query(value = "SELECT * FROM game_participant " +
            "WHERE event_id = :eventId AND user_id = :userId " +
            "ORDER BY last_gamed_at DESC " +
            "LIMIT 1", nativeQuery = true)
    GameParticipantEntity findMostRecentGameParticipant(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId
    );

    @Query("SELECT COUNT(gp.game.gameId) FROM GameParticipantEntity gp " +
            "WHERE gp.event.eventId = :eventId AND gp.user.userId = :userId")
    int countGamesByEventIdAndUserId(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId
    );

    @Query("""
    SELECT gp.user
    FROM GameParticipantEntity gp
    GROUP BY gp.user
    ORDER BY COUNT(gp) DESC
    """)
    List<UserEntity> findTopGameParticipant(Pageable pageable);


}
