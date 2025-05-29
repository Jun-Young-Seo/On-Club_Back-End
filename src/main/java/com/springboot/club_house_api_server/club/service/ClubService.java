package com.springboot.club_house_api_server.club.service;

import com.springboot.club_house_api_server.club.account.dto.ClubAccountDto;
import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.account.repository.ClubAccountRepository;
import com.springboot.club_house_api_server.club.dto.ModifyTagDto;
import com.springboot.club_house_api_server.club.dto.SearchClubResponseDto;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.guest.repository.GuestRepository;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;
    private final GuestRepository guestRepository;

    //모든 클럽 조회 메서드
    //이 메서드는 사용자 가입 여부와 관계 없이 모든 클럽 반환 --> FE 렌더링용
    //ex) 모든 동호회 살펴보기

    public ResponseEntity<List<SearchClubResponseDto>> getAllClubs() {
        List<SearchClubResponseDto> clubs = new ArrayList<>();
        List<ClubEntity> clubEntities = clubRepository.findAll();
        for (ClubEntity club : clubEntities) {
            SearchClubResponseDto clubDto = new SearchClubResponseDto();
            clubDto.setClub_id(club.getClubId());
            clubDto.setClubName(club.getClubName());
            clubDto.setClubDescription(club.getClubDescription());
            clubDto.setClubLogoURL(club.getClubLogoURL());
            clubDto.setClubBackgroundImageURL(club.getClubBackgroundURL());
            clubDto.setClubWhenCreated(club.getClubCreatedAt());
            clubDto.setTagOne(club.getClubTagOne());
            clubDto.setTagTwo(club.getClubTagTwo());
            clubDto.setTagThree(club.getClubTagThree());
            clubs.add(clubDto);
        }
        return ResponseEntity.ok(clubs);
    }

    //이름으로 클럽 찾기
    public ResponseEntity<?> getClubByName(String clubName) {
        Optional<ClubEntity> club = clubRepository.findByClubName(clubName);
        if (club.isPresent()) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    //동호회 신규 생성 - 중복 이름 생성 불가능 + 만든 사람은 자동 LEADER 배정
    public ResponseEntity<?> addClub(Long userId, String clubName, String clubDescription, String clubLogoURL, String clubBackgroundURL) {
        Optional<ClubEntity> clubOpt = clubRepository.findByClubName(clubName);
        if (clubOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 있는 클럽 이름입니다.");
        }
        Optional<UserEntity> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user ID에 해당하는 user가 없습니다.");
        }
        ClubEntity club = new ClubEntity(clubName, clubDescription);
        if(clubLogoURL!=null && !clubLogoURL.equals("")) {
            club.setClubLogoURL(clubLogoURL);
        }

        if(clubBackgroundURL!=null && !clubBackgroundURL.equals("")) {
            club.setClubBackgroundURL(clubBackgroundURL);
        }

        ClubEntity addedClub = clubRepository.save(club);
        membershipService.assignLeaderWhenClubCreated(userId,addedClub.getClubId());

        return ResponseEntity.ok(club);
    }

    //클럽 소속 모든 유저 찾기
    public ResponseEntity<?> findAllClubMembers(Long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(!clubOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("club Id에 해당하는 클럽이 없습니다.");
        }
        List<UserEntity> clubUsers = clubRepository.findAllClubMembers(clubId);
        List<List<String>> userInfos = new ArrayList<>();
        for(UserEntity user : clubUsers){
            List<String> userInfo = new ArrayList<>();
            userInfo.add(user.getUserName());
            userInfo.add(user.getUserTel());
            userInfos.add(userInfo);
        }
        return ResponseEntity.ok(userInfos);
    }


    public ResponseEntity<?> findAllClubsByUserId(long userId){
        List<ClubEntity> allClubs = clubRepository.findClubsByUserId(userId);
        List<SearchClubResponseDto> result = new ArrayList<>();
        for(ClubEntity club : allClubs) {
            SearchClubResponseDto responseDto = new SearchClubResponseDto();
            responseDto.setClub_id(club.getClubId());
            responseDto.setClubName(club.getClubName());
            responseDto.setClubDescription(club.getClubDescription());
            responseDto.setClubLogoURL(club.getClubLogoURL());
            responseDto.setClubBackgroundImageURL(club.getClubBackgroundURL());
            responseDto.setClubWhenCreated(club.getClubCreatedAt());

            result.add(responseDto);
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> findClubById(long id){
        Optional<ClubEntity> clubOpt = clubRepository.findById(id);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("club Id에 해당하는 클럽이 없습니다.");
        }
        SearchClubResponseDto dto = new SearchClubResponseDto();
        ClubEntity club = clubOpt.get();

        Long clubMemberCount = membershipRepository.countAllMemberships(id);
        Long guestCount = guestRepository.countByClub_ClubId(id);
        dto.setClub_id(club.getClubId());
        dto.setClubName(club.getClubName());
        dto.setClubDescription(club.getClubDescription());
        dto.setClubLogoURL(club.getClubLogoURL());
        dto.setClubBackgroundImageURL(club.getClubBackgroundURL());
        dto.setClubWhenCreated(club.getClubCreatedAt());
        dto.setClubDescriptionDetail(club.getClubDescriptionDetail());
        dto.setClubMemberCount(clubMemberCount);
        dto.setGuestCount(guestCount);
        dto.setTagOne(club.getClubTagOne());
        dto.setTagTwo(club.getClubTagTwo());
        dto.setTagThree(club.getClubTagThree());
        return ResponseEntity.ok(dto);
    }

    @Transactional
    //동호회 메인 계좌 지정 기능
    public ResponseEntity<?> setMainAccount(long clubId, long accountId){
        System.out.println("mainaccount modify "+clubId+accountId);
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 CLub이 없습니다.");
        }

        Optional<ClubAccountEntity> accountOpt = clubAccountRepository.findById(accountId);
        if(accountOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("account Id에 해당하는 account가 없습니다.");
        }


        clubOpt.get().setClubMainAccountId(accountId);
        clubRepository.save(clubOpt.get());

        return ResponseEntity.ok("mainAccount 지정 완료 : "+clubOpt.get().getClubMainAccountId());
    }

    public ResponseEntity<?> getMainAccount(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("club Id에 해당하는 club이 없습니다.");
        }

        ClubEntity club = clubOpt.get();
        if(club.getClubMainAccountId()==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("mainAccount가 지정되지 않은 club입니다.");
        }

        long mainAccountId = club.getClubMainAccountId();
        ClubAccountEntity mainAccount = clubAccountRepository.findAccountByAccountId(mainAccountId);

        ClubAccountDto response = ClubAccountDto.builder()
                .accountId(mainAccount.getAccountId())
                .clubId(club.getClubId())
                .accountName(mainAccount.getAccountName())
                .accountOwner(mainAccount.getAccountOwner())
                .bankName(mainAccount.getBankName())
                .accountNumber(mainAccount.getAccountNumber())
                .build();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> findAllTagsByClubId(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 club이 없습니다.");
        }

        List<String> tags = clubRepository.findTagsByClubId(clubId);
        return ResponseEntity.ok(tags);
    }

    public ResponseEntity<?> setClubTagsByClubId(ModifyTagDto modifyTagDto){
        Optional<ClubEntity> clubOpt = clubRepository.findById(modifyTagDto.getClubId());
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 club이 없습니다.");
        }

        ClubEntity club = clubOpt.get();
        club.setClubTagOne(modifyTagDto.getTagOne());
        club.setClubTagTwo(modifyTagDto.getTagTwo());
        club.setClubTagThree(modifyTagDto.getTagThree());
        clubRepository.save(club);

        return ResponseEntity.ok(club.getClubTagOne()+", "+club.getClubTagTwo()+", "+club.getClubTagThree());
    }
}
