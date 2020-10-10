package com.thelak.core.services;

import com.thelak.core.interfaces.ITokenService;
import com.thelak.core.models.UserInfo;
import com.thelak.route.exceptions.MsNotAuthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JWTTokenService implements ITokenService {

    private final String JWT_SECRET = "3b2648762a13d3f6be076edb7f70fa391e83049e1eaef30448eecc4effd31e74f7eaa092196868d677986ab5f12afd579a894d0daa0716da1d72c443a539976e";

    @Override
    public String generateToken(UserInfo user) {
        Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);
        Date expirationDate = Date.from(expirationTime);

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        String compactTokenString = Jwts.builder()
                .claim("id", user.getUserId())
                .claim("subscribe", user.isSubscribe())
                .claim("sub", user.getUserEmail())
                .claim("admin", user.isAdmin())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return "Bearer " + compactTokenString;
    }

    @Override
    public UserInfo parseToken(String token) throws ExpiredJwtException {
            byte[] secretBytes = JWT_SECRET.getBytes();

            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(secretBytes)
                    .build()
                    .parseClaimsJws(token);

            String email = jwsClaims.getBody()
                    .getSubject();
            Long userId = jwsClaims.getBody()
                    .get("id", Long.class);
            boolean isAdmin = jwsClaims.getBody().get("admin", Boolean.class);
            boolean isSubscribe = jwsClaims.getBody().get("subscribe", Boolean.class);

            return UserInfo.builder()
                    .userId(userId)
                    .userEmail(email)
                    .isAdmin(isAdmin)
                    .isSubscribe(isSubscribe)
                    .build();
    }
}
