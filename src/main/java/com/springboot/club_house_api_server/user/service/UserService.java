package com.springboot.club_house_api_server.user.service;

import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginResponseDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
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
                .build();
        userRepository.save(userEntity);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        Optional<UserEntity> user = userRepository.findByUserTel(loginRequestDto.getUserTel());
        if(!user.isPresent()){
            throw new IllegalArgumentException("존재하지 않는 전화번호입니다.");
        }
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        String accessToken = jwtTokenGenerator.createToken(loginRequestDto.getUserTel(), "ROLE_USER");
        String refreshToken = jwtTokenGenerator.createToken(loginRequestDto.getUserTel(), "ROLE_USER");

        return new LoginResponseDto(accessToken, refreshToken);
    }
}
