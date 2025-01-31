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
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch(SecurityException | MalformedJwtException e){
            log.info("Invalid JWT token",e);
        }catch(ExpiredJwtException e){
            log.info("Expired JWT token",e);
        }catch(UnsupportedJwtException e){
            log.info("Unsupported JWT token",e);
        }catch(IllegalArgumentException e){
            log.info("JWT claims string is empty",e);
        }
        return false;
    }
    private Claims parseClaims(String accessToken) {
        try{
            return Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
        }
        catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
    public String createToken(String userTel, String role){
        return Jwts.builder()
                .setSubject(userTel)
                .claim("auth",role)
                .setIssuedAt(new Date())
                //1hour 유효기간
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
