package com.ai.qa.gateway.common;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class JwtUtil {

    // @Value("${jwt.secret}")
    // private String secret;

    // @Value("${jwt.expiration}")
    // private long expiration;

    // public String extractUsername(String token) {
    //     return parseToken(token).getSubject();
    // }

    // private SecretKey getSigningKey() {
    //     return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    // }

    // public Claims parseToken(String token) throws JwtException {
    //     return Jwts.parser()
    //             .verifyWith(getSigningKey())
    //             .build()
    //             .parseSignedClaims(token)
    //             .getPayload();
    // }

    // public String generateToken(UserDetails userDetails) {
    //     return Jwts.builder()
    //             .subject(userDetails.getUsername())
    //             .issuedAt(new Date())
    //             .expiration(new Date(System.currentTimeMillis() + expiration))
    //             .signWith(getSigningKey(), Jwts.SIG.HS256)
    //             .compact();
    // }

    // public String generateToken(String username) {
    //     return Jwts.builder()
    //             .subject(username)
    //             .issuedAt(new Date())
    //             .expiration(new Date(System.currentTimeMillis() + expiration))
    //             .signWith(getSigningKey(), Jwts.SIG.HS256)
    //             .compact();
    // }

    // public Boolean validateToken(String token, UserDetails userDetails) {
    //     try {
    //         final String username = extractUsername(token);
    //         return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    //     } catch (Exception e) {
    //         return false;
    //     }
    // }

    // public boolean validateToken(String token) {
    //     try {
    //         Jwts.parser()
    //             .verifyWith(getSigningKey())
    //             .build()
    //             .parseSignedClaims(token);
    //         return true;
    //     } catch (JwtException | IllegalArgumentException e) {
    //         return false;
    //     }
    // }

    // private Boolean isTokenExpired(String token) {
    //     return extractExpiration(token).before(new Date());
    // }

    // private Date extractExpiration(String token) {
    //     return parseToken(token).getExpiration();
    // }

}
