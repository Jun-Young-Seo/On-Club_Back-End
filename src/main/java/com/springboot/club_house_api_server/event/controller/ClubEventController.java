package com.springboot.club_house_api_server.event.controller;

import com.springboot.club_house_api_server.event.dto.ClubEventDto;
import com.springboot.club_house_api_server.event.service.ClubEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class ClubEventController {
    private final ClubEventService clubEventService;

    //이벤트 추가
    @PostMapping("/add-event")
    public ResponseEntity<?> addEvent(@RequestBody ClubEventDto dto){
        return clubEventService.addEvent(dto.getClubId(), dto.getEventStartTime(), dto.getEventEndTime(), dto.getEventDescription());
    }

    //클럽 ID로 모든 이벤트 조회
    @GetMapping("/get-event/club_id")
    public ResponseEntity<?> getAllClubEvents(@RequestParam("clubId") long clubId){
        return clubEventService.getAllEvents(clubId);
    }

    //클럽 ID와 시간으로 특정 시간대의 모든 이벤트 조회
    @GetMapping("/get-event/date")
    public ResponseEntity<?> getEventsByDate(
            @RequestParam("clubId") long clubId,
            @RequestParam("eventStartTime")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime eventStartTime,
            @RequestParam("eventEndTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventEndTime){
            return clubEventService.getEventsByClubAndDate(clubId, eventStartTime, eventEndTime);
    }

    //클럽 ID와 시간, 키워드로 특정 시간대의 특정 키워드 포함하는 모든 이벤트 DB 조회
    @GetMapping("/get-event/keyword")
    public ResponseEntity<?> getEventsByDateAndKeyword(
            @RequestParam("clubId") long clubId,
            @RequestParam("eventStartTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventStartTime,
            @RequestParam("eventEndTime") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventEndTime,
            @RequestParam("keyWord")String keyWord) {
            return clubEventService.getEventsByClubAndDateAndKeyword(clubId, eventStartTime, eventEndTime, keyWord);
    }

    @GetMapping("/get-event/user_id")
    public ResponseEntity<?> getAllEventsWhereUserJoined(@RequestParam("userId") long userId){
        return clubEventService.getAllEventsWhereUserJoined(userId);
    }

    @GetMapping("/get-event/user_id_and_date")
    public ResponseEntity<?> getUserEventsWithinDateRange(
            @RequestParam("userId") long userId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return clubEventService.getEventsByUserAndDateRange(userId, startDate, endDate);
    }

}
