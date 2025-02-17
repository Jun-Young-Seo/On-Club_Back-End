package com.springboot.club_house_api_server.event.entity;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="club_event")
public class ClubEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_id")
    private long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="club_id",nullable=false)
    private ClubEntity club;

    @Column(name="start_time")
    private LocalDateTime eventStartTime;

    @Column(name="end_time")
    private LocalDateTime eventEndTime;

    @Column(name="description")
    private String eventDescription;


    public ClubEventEntity(ClubEntity club, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.club=club;
        this.eventStartTime = startTime;
        this.eventEndTime = endTime;
        this.eventDescription = description;
    }
}
