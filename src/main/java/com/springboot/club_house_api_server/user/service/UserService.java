package com.springboot.club_house_api_server.user.service;

import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginResponseDto;
import com.springboot.club_house_api_server.user.dto.UserInfoDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;

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


    //Login
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto){
        Optional<UserEntity> userOpt = userRepository.findByUserTel(loginRequestDto.getUserTel());
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 전화번호입니다.");
        }
        UserEntity user = userOpt.get();
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }
        long accessTokenValidity = 1000 * 60 * 15 * 4  ; // 15분 * 4 - accessToken
        long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7 ; // 24 * 7시간 - RefreshToken 확정 후 상수화 할 것
        String userId = String.valueOf(user.getUserId());
        //setSubject는 String형으로 받으므로 valueOf
        String accessToken = jwtTokenGenerator.createToken(userId, accessTokenValidity);
        String refreshToken = jwtTokenGenerator.createToken(userId, refreshTokenValidity);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        LoginResponseDto response =  new LoginResponseDto(userId,accessToken, refreshToken, user.getUserName());
        return ResponseEntity.ok(response);
    }

    //refresh Token
    public LoginResponseDto refreshToken(String refreshToken){
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

        long accessTokenValidity = 1000 * 60 * 15;
        String newAccessToken = jwtTokenGenerator.createToken(userId,  accessTokenValidity);

        return new LoginResponseDto(userId, newAccessToken, refreshToken, user.get().getUserName());
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
}
