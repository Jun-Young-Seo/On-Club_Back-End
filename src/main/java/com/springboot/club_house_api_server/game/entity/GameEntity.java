package com.springboot.club_house_api_server.game.entity;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name="game")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id",nullable = false)
    private ClubEventEntity event;

    @OneToMany(mappedBy = "game")
    private List<GameParticipantEntity> gameParticipants = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="club_id",nullable = false)
    private ClubEntity club;

    @Column(name = "start_at", nullable = true)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = true)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private GameStatus status;

    @Column(name = "score", nullable = true)
    private String score;

    public enum GameStatus {
        WAITING,
        PLAYING,
        DONE
    }
}
