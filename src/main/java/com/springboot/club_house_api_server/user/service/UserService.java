package com.springboot.club_house_api_server.user.service;

import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginResponseDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public void join(JoinRequestDto joinRequestDto){
        Optional<UserEntity> userExist = userRepository.findByUserTel(joinRequestDto.getUserTel());
        if(userExist.isPresent()){
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
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
    }
    //Login
    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        Optional<UserEntity> user = userRepository.findByUserTel(loginRequestDto.getUserTel());
        if(!user.isPresent()){
            throw new IllegalArgumentException("존재하지 않는 전화번호입니다.");
        }
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        long accessTokenValidity = 1000 * 60 * 15 * 4  ; // 15분 - accessToken
        long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7 ; // 24시간 - RefreshToken 확정 후 상수화 할 것
        String userId = String.valueOf(user.get().getUserId());
        //setSubject는 String형으로 받으므로 valueOf
        String accessToken = jwtTokenGenerator.createToken(userId, accessTokenValidity);
        String refreshToken = jwtTokenGenerator.createToken(userId, refreshTokenValidity);

        user.get().setRefreshToken(refreshToken);
        userRepository.save(user.get());
        return new LoginResponseDto(userId,accessToken, refreshToken);
    }

    //refresh Token
    public LoginResponseDto refreshToken(String refreshToken){
        System.out.println(refreshToken);
        if(refreshToken == null || !jwtTokenGenerator.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        String userId = jwtTokenGenerator.getUserId(refreshToken);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(userId));
        if(!user.isPresent()){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if(!user.get().getRefreshToken().equals(refreshToken)){
            throw new IllegalArgumentException("위조된 토큰일 수 있습니다.");
        }

        long accessTokenValidity = 1000 * 60 * 15;
        String newAccessToken = jwtTokenGenerator.createToken(userId,  accessTokenValidity);

        return new LoginResponseDto(userId, newAccessToken, refreshToken);
    }

    public String logout(String refreshToken){
        if(refreshToken == null || !jwtTokenGenerator.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        String userTel = jwtTokenGenerator.getUserId(refreshToken);
        Optional<UserEntity> user = userRepository.findByUserTel(userTel);

        if(!user.isPresent()){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if(!user.get().getRefreshToken().equals(refreshToken)){
            throw new IllegalArgumentException("위조된 토큰일 수 있습니다.");
        }

        user.get().setRefreshToken(null);
        userRepository.save(user.get());

        return userTel+" 정상적으로 로그아웃 됐습니다.";
    }
}
