package com.springboot.club_house_api_server.game.repository;

import com.springboot.club_house_api_server.game.entity.TeamEntity;
import com.springboot.club_house_api_server.game.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Long> {
    List<TeamMemberEntity> findByTeam(TeamEntity team);
}
