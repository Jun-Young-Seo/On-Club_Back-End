package com.springboot.club_house_api_server.jwt.generator;

import com.springboot.club_house_api_server.jwt.customobj.ClubUserDetails;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenGenerator {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);
    private final Key key;
    private final MembershipRepository membershipRepository;

    public JwtTokenGenerator(@Value("${jwt.secret}") String secretKey, MembershipRepository membershipRepository) {
        //HMAC-SHA 알고리즘 사용
        this.key= Keys.hmacShaKeyFor(secretKey.getBytes());
        this.membershipRepository = membershipRepository;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if(claims.get("roles")==null){
            throw new BadCredentialsException("Invalid access token");
        }

        // JWT에서 roles 클레임을 Map<Long, String> 형태로 가져오기
        // 이 때 가져온 키는 String 형태라 authentication 객체에 userRole이 null이 된다.
        Map<String, String> rawRoles = claims.get("roles", Map.class);

        Map<Long, String> userRoles = new HashMap<>();
        for (Map.Entry<String, String> entry : rawRoles.entrySet()) {
            userRoles.put(Long.parseLong(entry.getKey()), entry.getValue());
        }

        //sub는 userId로 설정해뒀음
        String userId = claims.getSubject();

        //ClubUserDetails는 UserDetails를 구현한 커스텀 구현체
        //./customobj/ClubUserDetails.class
        ClubUserDetails principal = new ClubUserDetails(userId, userRoles);
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    //토큰 검증용 메서드
    public boolean validateToken(String token) {
        //Jwt 파서에 키(DI 되어있음)를 이용해서 서명 검증
        //실패하면 파서단에서 예외가 터지므로 return false가 필요하지 않음
        //터진 예외는 ExceptionHandler에서 처리하도록 했음.
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        }
        catch (Exception e) {
            throw e;
        }
        return true;
    }

    //클레임 파싱 메서드
    private Claims parseClaims(String accessToken) {
        try{
            return Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
        }
        catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
    //토큰발급
    public String createToken(String userId, long expireTime){
        Map<Long,String> userRoles = getUserRoles(Long.parseLong(userId));

        return Jwts.builder()
                .setSubject(userId)
                .claim("roles",userRoles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserId(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //[1 : "LEADER"] 형태로 유저가 가입된 모든 클럽에 대해 권한 정보 반환
    //JWT에 클레임으로 담아서 반환하고, 접근권한 검증용으로 사용
    private Map<Long, String> getUserRoles(Long userId){
        List<MembershipEntity> membershipEntities = membershipRepository.findAllMembershipsByUserId(userId);
        Map<Long, String> userRoles = new HashMap<>();
        for(MembershipEntity membershipEntity : membershipEntities){
            userRoles.put(membershipEntity.getClub().getClubId(), membershipEntity.getRole().toString());
        }
        return userRoles;
    }

}
