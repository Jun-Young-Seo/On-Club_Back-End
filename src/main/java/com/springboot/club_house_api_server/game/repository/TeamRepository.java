package com.springboot.club_house_api_server.game.repository;

import com.springboot.club_house_api_server.game.entity.GameEntity;
import com.springboot.club_house_api_server.game.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    List<TeamEntity> findByGame(GameEntity game);
    Optional<TeamEntity> findByTeamId(Long teamId);
}
