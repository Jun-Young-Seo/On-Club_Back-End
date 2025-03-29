package com.springboot.club_house_api_server.game.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.game.dto.GameResponseDto;
import com.springboot.club_house_api_server.game.dto.GameStartDto;
import com.springboot.club_house_api_server.game.entity.GameEntity;
import com.springboot.club_house_api_server.game.entity.GameParticipantEntity;
import com.springboot.club_house_api_server.game.repository.GameParticipantRepository;
import com.springboot.club_house_api_server.game.repository.GameRepository;
import com.springboot.club_house_api_server.game.dto.CreateGameRequestDto;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.participant.service.ParticipantService;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    @PersistenceContext
    EntityManager entityManager;

    private final ClubEventRepository clubEventRepository;
    private final ClubRepository clubRepository;
    private final GameRepository gameRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;

    @Transactional
    public ResponseEntity<?> makeGame(CreateGameRequestDto dto) {
        ClubEventEntity event = clubEventRepository.findByEventId(dto.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
        ClubEntity club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("클럽을 찾을 수 없습니다."));

        List<UserEntity> users = new ArrayList<>();
        List<ParticipantEntity> participants = new ArrayList<>();
        System.out.println(dto.getClubId());
        System.out.println(dto.getEventId());
        System.out.println(dto.getUserIdList());
        for (Long userId : dto.getUserIdList()) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));
            ParticipantEntity participant = participantRepository
                    .findByUserIdAndEventId(userId, dto.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("이벤트 참가자 없음: " + userId));

            users.add(user);
            participants.add(participant);
        }

        GameEntity game = GameEntity.builder()
                .event(event)
                .club(club)
                .startAt(null)
                .status(GameEntity.GameStatus.WAITING)
                .score(null)
                .build();

        GameEntity savedGame = gameRepository.save(game);

        for(UserEntity user : users) {
            GameParticipantEntity gpe = GameParticipantEntity.builder()
                    .game(savedGame)
                    .user(user)
                    .event(event)
                    .lastGamedAt(null)
                    .build();
            gameParticipantRepository.save(gpe);
        }

        return ResponseEntity.ok(GameResponseDto.builder()
                .gameId(savedGame.getGameId())
                .eventId(event.getEventId())
                .clubId(club.getClubId())
                .startAt(savedGame.getStartAt())
                .endAt(savedGame.getEndAt())
                .status(savedGame.getStatus())
                .score(savedGame.getScore())
                .userIdList(users.stream().map(UserEntity::getUserId).toList())
                .build());
    }

    @Transactional
    public ResponseEntity<?> startGame(GameStartDto gameStartDto) {
//        System.out.println("+================STARTGAME======================");
        long gameId = gameStartDto.getGameId();
        List<Long> userIdList = gameStartDto.getUserIdList();

        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임 없음"));

        LocalDateTime now = LocalDateTime.now();
        game.setStartAt(now);
        game.setStatus(GameEntity.GameStatus.PLAYING);
        GameEntity savedGame = gameRepository.save(game);
        return ResponseEntity.ok(savedGame.getStartAt());
    }


    @Transactional
    public ResponseEntity<?> endGame(Long gameId, int score1, int score2) {
//        System.out.println("+================endGAme======================");

        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임 없음"));

        LocalDateTime now = LocalDateTime.now();
        game.setEndAt(now);
        game.setStatus(GameEntity.GameStatus.DONE);
        game.setScore(score1 + ":" + score2);

        gameRepository.save(game);

        List<GameParticipantEntity> gpes = gameParticipantRepository.findByGameId(gameId);
        for(GameParticipantEntity gpe : gpes) {
            gpe.setLastGamedAt(now);
            gameParticipantRepository.save(gpe);
        }
        entityManager.flush();
        return ResponseEntity.ok(game.getEndAt());
    }

    @Transactional
    public ResponseEntity<?> getAllGamesByEventId(Long eventId) {
        List<GameEntity> games = gameRepository.findAllGamesByEventId(eventId);
        List<GameResponseDto> response = new ArrayList<>();

        for (GameEntity game : games) {
            List<Long> userIds = gameParticipantRepository.findUserIdsByGameId(game.getGameId());
            response.add(GameResponseDto.builder()
                    .gameId(game.getGameId())
                    .eventId(game.getEvent().getEventId())
                    .clubId(game.getClub().getClubId())
                    .startAt(game.getStartAt())
                    .endAt(game.getEndAt())
                    .status(game.getStatus())
                    .score(game.getScore())
                    .userIdList(userIds)
                    .build());
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

}
