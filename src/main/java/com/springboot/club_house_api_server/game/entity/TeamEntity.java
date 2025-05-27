package com.springboot.club_house_api_server.game.entity;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="team")
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="game_id",nullable = false)
    private GameEntity game;

    @Column(name="start_at")
    private LocalDateTime startAt;

    @Column(name="end_at")
    private LocalDateTime endAt;

    @Column(name="team_number")
    private Integer teamNumber;  /// 1 or 2 - 유저가 어느 팀이었는지 구분용

    @Column(name="team_score")
    private Integer teamScore;

    @Column(name="is_win")
    private Boolean isWinner;

}
