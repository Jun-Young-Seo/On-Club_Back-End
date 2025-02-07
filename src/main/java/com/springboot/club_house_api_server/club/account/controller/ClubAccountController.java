package com.springboot.club_house_api_server.club.account.controller;

import com.springboot.club_house_api_server.club.account.dto.ClubAccountDto;
import com.springboot.club_house_api_server.club.account.service.ClubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class ClubAccountController {
    private final ClubAccountService clubAccountService;

    @PostMapping("/make")
    public ResponseEntity<?> makeAccount(@RequestBody ClubAccountDto clubAccountDto){
       return clubAccountService.makeNewAccount(
                clubAccountDto.getClubId(),
                clubAccountDto.getAccountName(),
                clubAccountDto.getAccountNumber(),
                clubAccountDto.getAccountOwner(),
                clubAccountDto.getBankName());
    }
}
