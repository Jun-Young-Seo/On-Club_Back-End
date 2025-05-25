package com.springboot.club_house_api_server.user.service;

import com.springboot.club_house_api_server.apn.entity.APNEntity;
import com.springboot.club_house_api_server.apn.repository.APNRepository;
import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.user.dto.*;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; // 15분 - accessToken
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7 ; // 24 * 7시간 - RefreshToken 확정 후 상수화 할 것
    private final APNRepository apnRepository;
    private final MembershipRepository membershipRepository;
    private final ParticipantRepository participantRepository;

    //회원가입
    public ResponseEntity<?> join(JoinRequestDto joinRequestDto){
        Optional<UserEntity> userExist = userRepository.findByUserTel(joinRequestDto.getUserTel());
        if(userExist.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 전화번호입니다.");
        }
        String hashPwd = passwordEncoder.encode(joinRequestDto.getPassword());

        UserEntity userEntity = UserEntity.builder()
                .userName(joinRequestDto.getUserName())
                .password(hashPwd)
                .userTel(joinRequestDto.getUserTel())
                .birthDate(joinRequestDto.getBirthDate())
                .career(joinRequestDto.getCareer())
                .gender(joinRequestDto.getGender())
                .region(joinRequestDto.getRegion())
                .build();
        userRepository.save(userEntity);

        return ResponseEntity.ok("회원가입 성공");
    }


    //Login For Web
    public ResponseEntity<?> loginForWeb(LoginRequestDtoForWeb loginRequestDto){
        Optional<UserEntity> userOpt = userRepository.findByUserTel(loginRequestDto.getUserTel());
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 전화번호입니다.");
        }
        UserEntity user = userOpt.get();
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }
        String userId = String.valueOf(user.getUserId());
        //setSubject는 String형으로 받으므로 valueOf
        String accessToken = jwtTokenGenerator.createToken(userId, ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtTokenGenerator.createToken(userId, REFRESH_TOKEN_VALIDITY);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        LoginResponseDto response =  new LoginResponseDto(userId,accessToken, refreshToken, user.getUserName());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> loginForIOS(LoginRequestDtoForIOS loginRequestDto){
        if(loginRequestDto.getDeviceToken().equals("none")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("none");
        }
        Optional<UserEntity> userOpt = userRepository.findByUserTel(loginRequestDto.getUserTel());
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 전화번호입니다.");
        }
        UserEntity user = userOpt.get();
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }
        String userId = String.valueOf(user.getUserId());
        //setSubject는 String형으로 받으므로 valueOf
        String accessToken = jwtTokenGenerator.createToken(userId, ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtTokenGenerator.createToken(userId, REFRESH_TOKEN_VALIDITY);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        String deviceToken = loginRequestDto.getDeviceToken();

        LocalDateTime now = LocalDateTime.now();

        //새 기기 발견
        Optional<APNEntity> apnOpt = apnRepository.findByUserIdAndDeviceToken(Long.valueOf(userId), deviceToken);
        if(apnOpt.isEmpty()) {
            APNEntity apnEntity = new APNEntity();
            apnEntity.setUser(user);
            apnEntity.setDeviceToken(deviceToken);
            apnEntity.setPlatform("IOS");
            apnEntity.setLastUsedAt(now);
            apnRepository.save(apnEntity);
        }
        else{
            apnOpt.get().setLastUsedAt(now);
            apnRepository.save(apnOpt.get());
        }
        LoginResponseDto response =  new LoginResponseDto(userId,accessToken, refreshToken, user.getUserName());
        return ResponseEntity.ok(response);
    }

    //refresh Token
    public ResponseEntity<?> refreshToken(String refreshToken){
        if(refreshToken == null || !jwtTokenGenerator.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("RefreshToken 검증 실패");
        }
        String userId = jwtTokenGenerator.getUserId(refreshToken);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(userId));
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("userId에 해당하는 User가 없습니다.");
        }

        if(!user.get().getRefreshToken().equals(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("위조된 토큰일 수 있습니다. DB 토큰과 불일치");
        }

        String newAccessToken = jwtTokenGenerator.createToken(userId,  ACCESS_TOKEN_VALIDITY);

        LoginResponseDto res = new LoginResponseDto(userId, newAccessToken, refreshToken, user.get().getUserName());

        return ResponseEntity.ok(res);
    }

    public String logout(String refreshToken){
        if(refreshToken == null || !jwtTokenGenerator.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        String userId = jwtTokenGenerator.getUserId(refreshToken);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(userId));

        if(user.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if(!user.get().getRefreshToken().equals(refreshToken)){
            throw new IllegalArgumentException("위조된 토큰일 수 있습니다.");
        }

        user.get().setRefreshToken(null);
        userRepository.save(user.get());

        return user.get().getUserName()+" 님이 정상적으로 로그아웃 됐습니다.";
    }

    //유저 정보 리턴
    public ResponseEntity<?> getUserInfo(long userId){
        Optional<UserEntity> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user ID에 해당하는 User가 없습니다.");
        }
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userTel(user.get().getUserTel())
                .userName(user.get().getUserName())
                .birthDate(user.get().getBirthDate())
                .career(user.get().getCareer())
                .gender(user.get().getGender())
                .region(user.get().getRegion())
                .build();
        return ResponseEntity.ok(userInfoDto);
    }

    public ResponseEntity<?> getUserInfoForMyPage(long userId, int year, int month){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user Id에 해당하는 user가 없습니다.");
        }
        if(month>12 || month<1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("날짜 설정 오류");
        }

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime startDate = firstDay.atStartOfDay();
        LocalDateTime endDate = lastDay.atTime(23, 59, 59);

        long countMemberships = membershipRepository.countMembershipsByUserId(userId);
        long countParticipateWithTime = participantRepository.countParticipantsByUserIdWithTime(userId,startDate,endDate);
        long countParticipate = participantRepository.countParticipantsByUserId(userId);

        MyPageDto myPageDto = new MyPageDto();
        myPageDto.setCountMemberships(countMemberships);
        myPageDto.setCountParticipantThisMonth(countParticipateWithTime);
        myPageDto.setCountAccumulateParticipant(countParticipate);

        return ResponseEntity.ok(myPageDto);
    }
}
