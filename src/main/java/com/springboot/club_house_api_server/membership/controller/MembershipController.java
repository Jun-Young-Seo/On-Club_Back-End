package com.springboot.club_house_api_server.membership.controller;

import com.springboot.club_house_api_server.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class MembershipController {
    private final MembershipService membershipService;
}
