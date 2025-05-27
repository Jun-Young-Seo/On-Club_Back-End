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

}
