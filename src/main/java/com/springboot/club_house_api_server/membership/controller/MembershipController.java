package com.springboot.club_house_api_server.membership.controller;

import com.springboot.club_house_api_server.membership.dto.MembershipJoinDto;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class MembershipController {
    private final MembershipService membershipService;

    @PostMapping("/join")
    public ResponseEntity<?> joinToClub(@RequestBody MembershipJoinDto membershipJoinDto){
        return membershipService.joinToClub(
                membershipJoinDto.getUserId(),
                membershipJoinDto.getClubId(),
                membershipJoinDto.getRole());
    }
}
