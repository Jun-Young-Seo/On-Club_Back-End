package com.springboot.club_house_api_server.jwt.generator;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenGenerator {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);
    private final Key key;

    public JwtTokenGenerator(@Value("${jwt.secret}") String secretKey) {
        //HMAC-SHA 알고리즘 사용
        this.key= Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if(claims.get("auth")==null){
            throw new BadCredentialsException("Invalid access token");
        }

        Collection<? extends GrantedAuthority> authorities
                = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
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
    public String createToken(String userId, String role, long expireTime){
        return Jwts.builder()
                .setSubject(userId)
                .claim("auth",role)
                .setIssuedAt(new Date())
                //1hour 유효기간
                .setExpiration(new Date(System.currentTimeMillis()+ expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUserTel(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
