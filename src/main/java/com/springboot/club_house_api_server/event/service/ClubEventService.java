package com.springboot.club_house_api_server.event.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.dto.ClubEventDto;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
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
public class ClubEventService {
    private final ClubEventRepository clubEventRepository;
    private final ClubRepository clubRepository;

    //이벤트 추가하기
    public ResponseEntity<?> addEvent(long clubId, LocalDateTime startTime, LocalDateTime endTime, String description){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        System.out.println("clubId : "+clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();
        ClubEventEntity clubEvent = new ClubEventEntity(club,startTime,endTime,description);

        clubEventRepository.save(clubEvent);
        return ResponseEntity.status(HttpStatus.OK).body("이벤트가 정상적으로 추가되었습니다.");
    }

    //클럽 ID로 모든 이벤트 받아오기
    public ResponseEntity<?> getAllEvents(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }
        List<ClubEventEntity> events = clubEventRepository.findAllEventsByClubId(clubId);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }

    //클럽 ID와 시작, 끝 시간으로 특정 시간대의 모든 이벤트 받아오기
    public ResponseEntity<?> getEventsByClubAndDate(long clubId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }

        List<ClubEventEntity> events = clubEventRepository.findAllEventsByClubIdAndTimeRange(clubId, startTime, endTime);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }


    //클럽 ID와 시작, 끝 시간, 키워드로 특정 시간대의 특정 키워드가 포함된 모든 이벤트 받아오기
    public ResponseEntity<?> getEventsByClubAndDateAndKeyword(long clubId, LocalDateTime startTime, LocalDateTime endTime, String keyword) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }

        List<ClubEventEntity> events = clubEventRepository.findAllEventsByClubIdAndDateAndKeyword(clubId, startTime, endTime,keyword);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

}
