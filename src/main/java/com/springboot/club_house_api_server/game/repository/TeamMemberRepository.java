package com.springboot.club_house_api_server.game.repository;

import com.springboot.club_house_api_server.game.dto.GamePlayStatDto;
import com.springboot.club_house_api_server.game.dto.ScoreStatDto;
import com.springboot.club_house_api_server.game.entity.TeamEntity;
import com.springboot.club_house_api_server.game.entity.TeamMemberEntity;
import com.springboot.club_house_api_server.participant.dto.UserFinishedTimeDto;
import com.springboot.club_house_api_server.report.dto.AttendanceStatDto;
import com.springboot.club_house_api_server.report.dto.GameStatDto;
import com.springboot.club_house_api_server.user.dto.UserInfoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Long> {
    List<TeamMemberEntity> findByTeam(TeamEntity team);

    //Match Component에서 사용할 마지막 게임참여시가 추적용
    @Query("""
    SELECT new com.springboot.club_house_api_server.participant.dto.UserFinishedTimeDto(
        u.userId,
        u.userName,
        MAX(t.endAt),
        u.career,
        u.gender,
        COUNT(g)
    )
    FROM TeamMemberEntity tm
    JOIN tm.user u
    JOIN tm.team t
    JOIN t.game g
    WHERE g.event.eventId = :eventId
    GROUP BY u.userId, u.career, u.gender
""")
    List<UserFinishedTimeDto> findAllParticipantInfoByEventId(@Param("eventId") Long eventId);


    //MemberReport에서 사용할 득점왕 상위 3명 쿼리
    @Query("""
SELECT new com.springboot.club_house_api_server.game.dto.ScoreStatDto(
  u.userName,
  u.userTel,
  u.gender,
  u.career,
  SUM(t.teamScore)
)
FROM TeamMemberEntity tm
JOIN tm.user u
JOIN tm.team t
JOIN t.game g
WHERE g.endAt BETWEEN :start AND :end
  AND g.club.clubId = :clubId
GROUP BY u.userId, u.userName, u.userTel, u.gender, u.career
ORDER BY SUM(t.teamScore) DESC
""")
    List<ScoreStatDto> findTopScoringUsersInClubBetween(
            @Param("clubId") Long clubId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

//MemberReport에서  사용할 게임 참가 가장 많이 한 상위 3명 유저찾기
@Query("""
SELECT new com.springboot.club_house_api_server.game.dto.GamePlayStatDto(
  u.userName,
  u.userTel,
  u.gender,
  u.career,
  COUNT(DISTINCT g)
)
FROM TeamMemberEntity tm
JOIN tm.user u
JOIN tm.team t
JOIN t.game g
WHERE g.startAt BETWEEN :start AND :end
  AND g.club.clubId = :clubId
GROUP BY u.userId, u.userName, u.userTel, u.gender, u.career
ORDER BY COUNT(DISTINCT g) DESC
""")
List<GamePlayStatDto> findTopPlayedUsersInClubBetween(
        @Param("clubId") Long clubId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
);


}
