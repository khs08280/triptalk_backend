package com.triptalk.triptalk.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration}")
  private Long EXPIRATION_TIME;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    Date expiration = extractClaim(token, Claims::getExpiration);
    if (expiration == null) {
      throw new IllegalArgumentException("Expiration time is missing in the token.");
    }
    return expiration;
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts
              .parserBuilder()
              .setSigningKey(getSignKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token.", e);
    }
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }


  public String generateToken(String userName) {
    if (EXPIRATION_TIME == null || EXPIRATION_TIME <= 0) {
      throw new IllegalArgumentException("Expiration time is not set correctly.");
    }
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userName);
  }

  private String createToken(Map<String, Object> claims, String userName) {
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(userName)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
  }

  public Boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (Exception e) {
      // 토큰이 잘못된 경우 예외 처리
      return false;
    }
  }

  private Key getSignKey() {
    if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
      throw new IllegalArgumentException("JWT secret key is not configured.");
    }
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}