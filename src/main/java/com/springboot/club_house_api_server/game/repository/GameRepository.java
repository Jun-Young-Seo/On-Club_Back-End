package com.springboot.club_house_api_server.game.repository;

import com.springboot.club_house_api_server.game.entity.GameEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
    @Query("SELECT g FROM GameEntity g WHERE g.event.eventId = :eventId")
    List<GameEntity> findAllGamesByEventId(@Param("eventId") Long eventId);


    // GameParticipantRepository
    @Query("SELECT gp.user FROM GameParticipantEntity gp WHERE gp.game.gameId = :gameId")
    List<UserEntity> findUsersByGameId(@Param("gameId") Long gameId);

    // 혹은 userId만 원할 경우
    @Query("SELECT gp.user.userId FROM GameParticipantEntity gp WHERE gp.game.gameId = :gameId")
    List<Long> findUserIdsByGameId(@Param("gameId") Long gameId);


}
