package com.springboot.club_house_api_server.club.account.service;

import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;

import com.springboot.club_house_api_server.club.account.repository.ClubAccountRepository;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.jwt.customobj.ClubUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubAccountService {
    private final ClubAccountRepository clubAccountRepository;
    private final ClubRepository clubRepository;

    public ResponseEntity<?> makeNewAccount(long clubId, String accountName, String accountNumber,
                                            String accountOwner, String bankName){
        Optional<ClubEntity> clubEntity = clubRepository.findById(clubId);
        if(!clubEntity.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club Id에 해당하는 클럽이 없습니다.");
        }

        ClubEntity club = clubEntity.get();
        ClubAccountEntity clubAccountEntity = new ClubAccountEntity(club, accountName, accountNumber, accountOwner, bankName);

        boolean accountExists = clubAccountRepository.isAlreadyExistAccount(club, accountName);
        if (accountExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 클럽에 " + accountName + " 계좌가 존재합니다.");
        }
        clubAccountRepository.save(clubAccountEntity);

        return ResponseEntity.ok(club.getClubName()+"에 새로운 계좌 "+accountName+"이 생성되었습니다.");
    }

    public boolean hasClubRole(Authentication authentication, Long clubId, List<String> requiredRoles) {
        System.out.println(authentication.getPrincipal().toString());
        if (authentication.getPrincipal() instanceof ClubUserDetails principal) {
            String userRole = principal.getRoleForClub(clubId);
            System.out.println(userRole);
            if(userRole != null){
                return requiredRoles.contains(userRole);
            }
        }
        return false;
    }

}
