package com.springboot.club_house_api_server.participant.service;

import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.game.entity.GameParticipantEntity;
import com.springboot.club_house_api_server.game.repository.GameParticipantRepository;
import com.springboot.club_house_api_server.game.repository.TeamMemberRepository;
import com.springboot.club_house_api_server.guest.entity.GuestEntity;
import com.springboot.club_house_api_server.guest.repository.GuestRepository;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.participant.dto.FindAllEventsDto;
import com.springboot.club_house_api_server.participant.dto.UserFinishedTimeDto;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final MembershipRepository membershipRepository;
    private final GuestRepository guestRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public ResponseEntity<?> joinToEvent(long userId, long eventId) {
        boolean isMember = false;
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("eventId에 해당하는 이벤트가 없습니다.");
        }
        ClubEventEntity event = eventOpt.get();

        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("userId에 해당하는 유저가 없습니다.");
        }
        if (participantRepository.existsByUserIdAndEventId(userId, eventId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 이벤트에 참가한 유저입니다.");
        }

        UserEntity user = userOpt.get();

        long clubId = event.getClub().getClubId();

        Optional<MembershipEntity>membershipOpt = membershipRepository.getMembershipEntityByUserIdAndClubId(userId,clubId);

        if(membershipOpt.isPresent()){
            isMember=true;
        }
        if(isMember) {
            MembershipEntity membership = membershipOpt.get();

            membership.setAttendanceCount(membership.getAttendanceCount() + 1);
            membershipRepository.save(membership);

            ParticipantEntity p = ParticipantEntity.builder()
                    .user(user)
                    .event(event)
                    .club(event.getClub())
                    .build();
            participantRepository.save(p);
        }
        else{ //Guest
            Optional<GuestEntity> guestOpt =  guestRepository.findByUserAndEvent(user, event);
            if(guestOpt.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserID에 해당하는 게스트 정보가 없습니다.");
            }
            ParticipantEntity p = ParticipantEntity.builder()
                    .user(user)
                    .event(event)
                    .club(event.getClub())
                    .build();
            participantRepository.save(p);
        }
        return ResponseEntity.ok("");
    }

    public ResponseEntity<?> getAllParticipantsByEventId(long eventId) {
        Optional<ClubEventEntity> clubEventOpt = clubEventRepository.findById(eventId);
        if (clubEventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event를 찾을 수 없습니다.");
        }


        List<UserFinishedTimeDto> finishedTimeDtoList = teamMemberRepository.findAllParticipantInfoByEventId(eventId);
        return ResponseEntity.ok(finishedTimeDtoList);
    }

    public ResponseEntity<?> findAllEventsByUserId(long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user ID에 해당하는 User가 없습니다.");
        }
        List<ClubEventEntity> allEventsIds = participantRepository.findAllEventIdsByUserId(userId);
        List<FindAllEventsDto> response = new ArrayList<>();
        //Membership으로써 참가하게 된 경우
        for(ClubEventEntity c : allEventsIds){
            FindAllEventsDto fDto = new FindAllEventsDto();
            fDto.setUserId(userId);
            fDto.setEventId(c.getEventId());
            fDto.setEventDescription(c.getEventDescription());
            fDto.setEventStartTime(c.getEventStartTime());
            fDto.setEventEndTime(c.getEventEndTime());
            fDto.setClubName(c.getClub().getClubName());
            response.add(fDto);
        }

        List<Long> guestEvents = guestRepository.findAllGuestEventsByUserId(userId);
        for(Long g : guestEvents){
            ClubEventEntity c = clubEventRepository.findById(g).get();
            FindAllEventsDto fDto = new FindAllEventsDto();
            fDto.setEventId(c.getEventId());
            fDto.setEventDescription(c.getEventDescription());
            fDto.setEventStartTime(c.getEventStartTime());
            fDto.setEventEndTime(c.getEventEndTime());
            fDto.setClubName(c.getClub().getClubName());
            fDto.setUserId(userId);
            response.add(fDto);
        }
        return ResponseEntity.ok(response);
    }


}
