package com.springboot.club_house_api_server.game.entity;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id", nullable = false)
    private ClubEventEntity event;

    @Column(name="last_gamed_at", nullable = true)
    private LocalDateTime lastGamedAt;

}
