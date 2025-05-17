package com.springboot.club_house_api_server.club.account.controller;

import com.springboot.club_house_api_server.club.account.dto.ClubAccountDto;
import com.springboot.club_house_api_server.club.account.service.ClubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class ClubAccountController {
    private final ClubAccountService clubAccountService;

    @PostMapping("/make")
    public ResponseEntity<?> makeAccount(@RequestBody ClubAccountDto clubAccountDto, Authentication authentication) {
        List<String> allowedRoles = List.of("MANAGER", "LEADER");

        boolean canAccess = clubAccountService.hasClubRole(authentication, clubAccountDto.getClubId(), allowedRoles);
        if(!canAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 클럽에 계좌를 생성할 권한이 없습니다.");
        }

       return clubAccountService.makeNewAccount(
                clubAccountDto.getClubId(),
                clubAccountDto.getAccountName(),
                clubAccountDto.getAccountNumber(),
                clubAccountDto.getAccountOwner(),
                clubAccountDto.getBankName());
    }

    @GetMapping("/get-all_accounts")
    public ResponseEntity<?> getAllAccounts(@RequestParam long clubId, Authentication authentication) {
        List<String> allowedRoles = List.of("MANAGER", "LEADER");
        boolean canAccess = clubAccountService.hasClubRole(authentication, clubId, allowedRoles);
        if(!canAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 클럽의 계좌를 조회할 권한이 없습니다.");
        }
        return clubAccountService.getAllClubAccounts(clubId);
    }
}
