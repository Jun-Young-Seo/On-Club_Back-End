package com.springboot.club_house_api_server.participant.service;

import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.game.entity.GameParticipantEntity;
import com.springboot.club_house_api_server.game.repository.GameParticipantRepository;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.participant.dto.ParticipantResponseDto;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubEventRepository clubEventRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public ResponseEntity<?> joinToEvent(long userId, long eventId) {


        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("eventId에 해당하는 이벤트가 없습니다.");
        }
        ClubEventEntity event = eventOpt.get();

        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("userId에 해당하는 유저가 없습니다.");
        }

        UserEntity user = userOpt.get();

        long clubId = event.getClub().getClubId();

        Optional<MembershipEntity>membershipOpt = membershipRepository.getMembershipEntityByUserIdAndClubId(userId,clubId);

        if(membershipOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MembershipId에 해당하는 membership이 없습니다.");
        }
        MembershipEntity membership = membershipOpt.get();

        membership.setAttendanceCount(membership.getAttendanceCount()+1);
        membershipRepository.save(membership);

        ParticipantEntity p = ParticipantEntity.builder()
                .user(user)
                .event(event)
                .club(event.getClub())
                .build();

        participantRepository.save(p);

        return ResponseEntity.ok("");
    }

    public ResponseEntity<?> getAllParticipantsByEventId(long eventId) {
        List<ParticipantEntity> participantEntityList = participantRepository.findByEventId(eventId);
        List<ParticipantResponseDto> response = new ArrayList<>();
        for(ParticipantEntity participantEntity : participantEntityList) {
            UserEntity user = participantEntity.getUser();
            ParticipantResponseDto dto = ParticipantResponseDto.builder()
                    .userId(user.getUserId())
                    .lastGamedAt(LocalDateTime.now())
                    .userName(user.getUserName())
                    .gender(user.getGender())
                    .career(user.getCareer())
                    .build();
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateParticipants(long eventId){
        List<ParticipantEntity> participantEntityList = participantRepository.findByEventId(eventId);
        List<ParticipantResponseDto> response = new ArrayList<>();
        for(ParticipantEntity participantEntity : participantEntityList) {
            UserEntity user = participantEntity.getUser();
            GameParticipantEntity gpe = gameParticipantRepository.findMostRecentGameParticipant(user.getUserId(),eventId);
            int gameCount = gameParticipantRepository.countGamesByEventIdAndUserId(eventId,user.getUserId());
            LocalDateTime lastGame = null;
            if (gpe != null) {
                lastGame = gpe.getLastGamedAt();
            }
            ParticipantResponseDto dto = ParticipantResponseDto.builder()
                    .userId(user.getUserId())
                    .lastGamedAt(lastGame)
                    .userName(user.getUserName())
                    .gender(user.getGender())
                    .career(user.getCareer())
                    .gameCount(gameCount)
                    .build();
            response.add(dto);
        }
        return ResponseEntity.ok(response);

    }
}
