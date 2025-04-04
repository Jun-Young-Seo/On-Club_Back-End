package com.springboot.club_house_api_server.guest.controller;

import com.springboot.club_house_api_server.guest.dto.GuestRequestDto;
import com.springboot.club_house_api_server.guest.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    @PostMapping("/attend/request")
    public ResponseEntity<?> attendEventAsGuest(@RequestBody GuestRequestDto dto){
        return guestService.attendEventAsGuest(dto.getUserId(), dto.getEventId());
    }

    @PostMapping("/attend/approve")
    public ResponseEntity<?> approveGuest(@RequestBody GuestRequestDto dto){
        return guestService.approveGuest(dto.getUserId(), dto.getEventId());
    }

    @PostMapping("/attend/reject")
    public ResponseEntity<?> rejectGuest(@RequestBody GuestRequestDto dto){
        return guestService.rejectGuest(dto.getUserId(), dto.getEventId());
    }
}
