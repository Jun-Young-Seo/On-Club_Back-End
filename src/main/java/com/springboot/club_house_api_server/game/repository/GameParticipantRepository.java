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



    @Query("""
    SELECT gp.user
    FROM GameParticipantEntity gp
    GROUP BY gp.user
    ORDER BY COUNT(gp) DESC
    """)
    List<UserEntity> findTopGameParticipant(Pageable pageable);


}
