package com.springboot.club_house_api_server.guest.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.guest.entity.GuestEntity;
import com.springboot.club_house_api_server.guest.repository.GuestRepository;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubEventRepository clubEventRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public ResponseEntity<?> attendEventAsGuest(long userId, long eventId){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user Id에 해당하는 User가 없습니다.");
        }
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if(eventOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event Id에 해당하는 Event가 없습니다.");
        }
        ClubEntity club = eventOpt.get().getClub();
        Optional<MembershipEntity> membershipOpt = membershipRepository.checkAlreadyJoined(userId, club.getClubId());
        if(membershipOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 클럽에 가입한 유저입니다.");
        }

        Optional<GuestEntity> alreadyAttend = guestRepository.findByUserAndEvent(userOpt.get(), eventOpt.get());
        if(alreadyAttend.isPresent()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 참석한 유저입니다.");
        }
        GuestEntity guestEntity = new GuestEntity();
        guestEntity.setClub(club);
        guestEntity.setUser(userOpt.get());
        guestEntity.setEvent(eventOpt.get());

        guestRepository.save(guestEntity);

        club.setClubAccumulatedGuests(club.getClubAccumulatedGuests() + 1);
        return ResponseEntity.ok("userId : "+ userId+"의 "+"eventId : " +eventId+"참석 처리 완료");
    }
}
