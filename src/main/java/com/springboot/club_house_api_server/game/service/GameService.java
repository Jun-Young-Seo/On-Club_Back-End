package com.springboot.club_house_api_server.game.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.game.dto.EndGameDto;
import com.springboot.club_house_api_server.game.dto.GameResponseDto;
import com.springboot.club_house_api_server.game.dto.GetGameDto;
import com.springboot.club_house_api_server.game.entity.GameEntity;
import com.springboot.club_house_api_server.game.entity.GameParticipantEntity;
import com.springboot.club_house_api_server.game.entity.TeamEntity;
import com.springboot.club_house_api_server.game.entity.TeamMemberEntity;
import com.springboot.club_house_api_server.game.repository.GameParticipantRepository;
import com.springboot.club_house_api_server.game.repository.GameRepository;
import com.springboot.club_house_api_server.game.dto.CreateGameRequestDto;
import com.springboot.club_house_api_server.game.repository.TeamMemberRepository;
import com.springboot.club_house_api_server.game.repository.TeamRepository;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.participant.service.ParticipantService;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

    private final ClubEventRepository clubEventRepository;
    private final ClubRepository clubRepository;
    private final GameRepository gameRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public ResponseEntity<?> makeGame(CreateGameRequestDto dto) {
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findByEventId(dto.getEventId());
        if(eventOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event를 찾을 수 없습니다.");
        }
        Optional<ClubEntity> clubOpt = clubRepository.findById(dto.getClubId());
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("club을 찾을 수 없습니다.");
        }
        ClubEventEntity event = eventOpt.get();
        ClubEntity club = clubOpt.get();

        GameEntity game = GameEntity.builder()
                .event(event)
                .club(club)
                .startAt(null)
                .endAt(null)
                .status(GameEntity.GameStatus.WAITING)
                .build();

        GameEntity savedGame = gameRepository.save(game);

        //팀 생성하고 팀원 등록
        Long teamOneId = saveTeamWithMembers(savedGame, 1, dto.getUserIdListForTeamOne(), dto.getEventId());
        Long teamTwoId = saveTeamWithMembers(savedGame, 2, dto.getUserIdListForTeamTwo(), dto.getEventId());

        List<Long> userIdList = new ArrayList<>();
        userIdList.addAll(dto.getUserIdListForTeamOne());
        userIdList.addAll(dto.getUserIdListForTeamTwo());

        return ResponseEntity.ok(GameResponseDto.builder()
                .gameId(savedGame.getGameId())
                .eventId(event.getEventId())
                .clubId(club.getClubId())
                .startAt(savedGame.getStartAt())
                .endAt(savedGame.getEndAt())
                .status(savedGame.getStatus())
                .userIdList(userIdList)
                .teamOneId(teamOneId)
                .teamTwoId(teamTwoId)
                .build());
    }

    @Transactional
    public ResponseEntity<?> startGame(Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다."));

        if (game.getStatus() != GameEntity.GameStatus.WAITING) {
            throw new IllegalStateException("게임이 대기 상태가 아닙니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        game.setStartAt(now);
        game.setStatus(GameEntity.GameStatus.PLAYING);

        // Team, TeamMember 시간 갱신
        //사실 아직 필요없지만 나중에 필요할까바 추가한 필드
        List<TeamEntity> teams = teamRepository.findByGame(game);
        for (TeamEntity team : teams) {
            team.setStartAt(now);
            teamRepository.save(team);

            List<TeamMemberEntity> members = teamMemberRepository.findByTeam(team);
            for (TeamMemberEntity member : members) {
                member.setJoinedAt(now);
                teamMemberRepository.save(member);
            }
        }

        return ResponseEntity.ok("게임이 시작되었습니다.");
    }



    @Transactional
    public ResponseEntity<?> endGame(EndGameDto dto) {
        Optional<GameEntity> gameOpt = gameRepository.findById(dto.getGameId());
        if (gameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("game을 찾을 수 없습니다.");
        }
        GameEntity game = gameOpt.get();

        LocalDateTime now = LocalDateTime.now();
        game.setEndAt(now);
        game.setStatus(GameEntity.GameStatus.DONE);

        Optional<TeamEntity> teamOneOpt = teamRepository.findByTeamId(dto.getTeamOneId());
        Optional<TeamEntity> teamTwoOpt = teamRepository.findByTeamId(dto.getTeamTwoId());
        if (teamOneOpt.isEmpty() || teamTwoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("team을 찾을 수 없습니다.");
        }
        TeamEntity teamOne = teamOneOpt.get();
        TeamEntity teamTwo = teamTwoOpt.get();

        teamOne.setEndAt(now);
        teamTwo.setEndAt(now);

        teamOne.setTeamScore(dto.getTeamOneScore());
        teamTwo.setTeamScore(dto.getTeamTwoScore());

        boolean isTeamOneWinner=false;
        boolean isTeamTwoWinner=true;
        if(dto.getTeamOneScore() > dto.getTeamTwoScore()){
            isTeamOneWinner=true;
            isTeamTwoWinner=false;
        }
        teamOne.setIsWinner(isTeamOneWinner);
        teamTwo.setIsWinner(isTeamTwoWinner);

        teamRepository.save(teamOne);
        teamRepository.save(teamTwo);


        List<TeamMemberEntity> teamOneMembers = teamMemberRepository.findByTeam(teamOne);
        List<TeamMemberEntity> teamTwoMembers = teamMemberRepository.findByTeam(teamTwo);

        for (TeamMemberEntity member : teamOneMembers) {
            member.setFinishedAt(now);
            teamMemberRepository.save(member);
        }
        for (TeamMemberEntity member : teamTwoMembers) {
            member.setFinishedAt(now);
            teamMemberRepository.save(member);
        }

        return ResponseEntity.ok(game.getEndAt());

    }

    @Transactional
    public ResponseEntity<?> getAllGamesByEventId(Long eventId) {
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event를 찾을 수 없습니다.");
        }
        List<GameEntity> games = gameRepository.findAllGamesByEventId(eventId);
        List<GetGameDto> response = new ArrayList<>();

        for (GameEntity game : games) {
            List<TeamEntity> teams = teamRepository.findByGame(game);
            if(teams.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("team이 없습니다.");
            }

            TeamEntity teamOne = teams.get(0);
            Long teamOneId = teamOne.getTeamId();
            TeamEntity teamTwo = teams.get(1);
            Long teamTwoId = teamTwo.getTeamId();
            List<TeamMemberEntity> teamOneMembers = teamMemberRepository.findByTeam(teamOne);
            List<TeamMemberEntity> teamTwoMembers = teamMemberRepository.findByTeam(teamTwo);
            List<String> userNames = new ArrayList<>();
            for (TeamMemberEntity m : teamOneMembers) {
                userNames.add(m.getUser().getUserName());
            }
            for (TeamMemberEntity m : teamTwoMembers) {
                userNames.add(m.getUser().getUserName());
            }
            // joinedAt = 가장 빠른 시간, finishedAt = 가장 늦은 시간
            LocalDateTime joinedAt = null;
            LocalDateTime finishedAt = null;

            for (TeamMemberEntity m : teamOneMembers) {
                LocalDateTime joined = m.getJoinedAt();
                LocalDateTime finished = m.getFinishedAt();

                if (joined != null && (joinedAt == null || joined.isBefore(joinedAt))) {
                    joinedAt = joined;
                }
                if (finished != null && (finishedAt == null || finished.isAfter(finishedAt))) {
                    finishedAt = finished;
                }
            }

            for (TeamMemberEntity m : teamTwoMembers) {
                LocalDateTime joined = m.getJoinedAt();
                LocalDateTime finished = m.getFinishedAt();

                if (joined != null && (joinedAt == null || joined.isBefore(joinedAt))) {
                    joinedAt = joined;
                }
                if (finished != null && (finishedAt == null || finishedAt.isBefore(finished))) {
                    finishedAt = finished;
                }
            }
            GetGameDto dto = GetGameDto.builder()
                    .gameId(game.getGameId())
                    .userNames(userNames)
                    .joinedAt(joinedAt)
                    .finishedAt(finishedAt)
                    .teamOneScore(teamOne.getTeamScore())
                    .teamTwoScore(teamTwo.getTeamScore())
                    .teamOneId(teamOneId)
                    .teamTwoId(teamTwoId)
                    .build();

            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<?> deleteGame(Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임입니다. gameId: " + gameId));

        // 1. 게임에 속한 GameParticipant 삭제
        List<GameParticipantEntity> gameParticipants = gameParticipantRepository.findByGameId(gameId);
        gameParticipantRepository.deleteAll(gameParticipants);

        // 2. 게임 자체 삭제
        gameRepository.delete(game);

        return ResponseEntity.ok("게임이 성공적으로 삭제되었습니다. gameId: " + gameId);
    }

    private Long saveTeamWithMembers(GameEntity game, int teamNumber, List<Long> userIdList, Long eventId) {
        // 팀 생성
        TeamEntity team = TeamEntity.builder()
                .game(game)
                .teamNumber(teamNumber)
                .startAt(null)
                .endAt(null)
                .teamScore(null)
                .isWinner(null)
                .build();
        TeamEntity savedTeam = teamRepository.save(team);

        //각 유저에 대해 팀멤버 등록
        for (Long userId : userIdList) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));

            // 해당 이벤트에 유저가 참가 중인지 확인
            participantRepository.findByUserIdAndEventId(userId, eventId)
                    .orElseThrow(() -> new IllegalArgumentException("이벤트 참가자 아님: " + userId));

            // 팀멤버 생성
            TeamMemberEntity teamMember = TeamMemberEntity.builder()
                    .team(savedTeam)
                    .user(user)
                    .joinedAt(null)
                    .finishedAt(null)
                    .build();
            teamMemberRepository.save(teamMember);
        }
        return team.getTeamId();
    }

}
