package com.springboot.club_house_api_server.participant.controller;


import com.springboot.club_house_api_server.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final ParticipantService participantService;

    @PostMapping("/join")
    public ResponseEntity<?> joinToEvent(@RequestParam long userId, @RequestParam long eventId) {
        return participantService.joinToEvent(userId, eventId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getWaits(@RequestParam long eventId) {
        return participantService.getAllParticipantsByEventId(eventId);
    }

    @GetMapping("/update")
    public ResponseEntity<?> updateParticipant( @RequestParam long eventId) {
        return participantService.updateParticipants(eventId);
    }
}
