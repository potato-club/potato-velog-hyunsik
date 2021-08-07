package com.velog.veloguser.security.jwt;

import com.velog.veloguser.exception.JwtTokenException;
import com.velog.veloguser.security.CustomUserDetailsService;
import com.velog.veloguser.security.PrincipalDetails;
import io.jsonwebtoken.*;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;


@Slf4j
@Configuration
public class TokenProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final Environment env;


    public TokenProvider(CustomUserDetailsService customUserDetailsService, Environment env) {
        this.customUserDetailsService = customUserDetailsService;
        this.env = env;
    }

    public String createToken(String userId) {

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(Objects.requireNonNull(env.getProperty("token.expiration_time")))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
    }


    public Authentication getAuthentication(String token) throws NotFoundException {

        Claims claims = getToken(token);
        String userId = claims.getSubject();

        String email = customUserDetailsService.findEmailByUserId(userId);
        PrincipalDetails principal = customUserDetailsService.loadUserByUsername(email);


        return new UsernamePasswordAuthenticationToken(principal, token, null);
    }

    //토큰에서 값 추출
    public Claims getToken(String token) {
        return Jwts.parser()
                .setSigningKey(env.getProperty("token.secret"))
                .parseClaimsJws(token)
                .getBody();
    }

    //토큰에서 값 추출
    public String validateTokenAndGetUserId(String token) {
        if (!validateToken(token)) {
            throw new JwtTokenException("토큰 오류 입니다.");
        }
        try {
            token = removeBear(token);
            Claims claims = Jwts.parser().setSigningKey(env.getProperty("token.secret")).parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new JwtTokenException("토큰 파싱 오류 입니다.");
        }
    }

    public boolean validateToken(String token) {
        try {
            token = removeBear(token);
            Jwts.parser().setSigningKey(env.getProperty("token.secret")).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private String removeBear(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }



}
