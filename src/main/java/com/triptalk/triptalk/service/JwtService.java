package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  private final UserRepository userRepository;

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


  public String generateToken(String username) {
    if (EXPIRATION_TIME == null || EXPIRATION_TIME <= 0) {
      throw new IllegalArgumentException("Expiration time is not set correctly.");
    }
    User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다."));

    Map<String, Object> claims = new HashMap<>();
    claims.put("userId",user.getId());
    return createToken(claims, username);
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

//  public void validateToken(String token, UserDetails userDetails){
//    try {
//      final String username = extractUsername(token);
//      // 원하는 조건(서명, 만료여부 등)
//      if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
//        throw new JwtException("토큰 검증 실패");
//      }
//    } catch (Exception e) {
//      throw new JwtException("토큰 파싱/서명 검증 에러", e);
//    }
//  }
public void validateToken(String token) {
  try {
    // parseClaimsJws: 서명 검증 + 만료 시간 검증
    Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token);

    // parse 단계에서 예외가 발생하지 않으면 "서명 및 기본 만료 시간" 검증 OK

  } catch (ExpiredJwtException e) {
    // 토큰이 만료된 경우
    log.error("JWT Token 만료됨: {}", e.getMessage());
    throw new JwtException("만료된 토큰입니다.", e);
  } catch (SignatureException e) {
    // 서명(Signature)이 유효하지 않은 경우
    log.error("JWT 서명 검증 실패: {}", e.getMessage());
    throw new JwtException("유효하지 않은 서명입니다.", e);
  } catch (JwtException e) {
    // 그 밖에 잘못된 토큰 구조 등
    log.error("JWT 검증 실패: {}", e.getMessage());
    throw new JwtException("유효하지 않은 토큰입니다.", e);
  }
}

  public Long getUserId(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(getSignKey())
              .build()
              .parseClaimsJws(token)
              .getBody();

      // 예: JWT payload에 "userId" 라는 클레임을 담아두었다고 가정
      return claims.get("userId", Long.class);

    } catch (ExpiredJwtException e) {
      log.error("JWT Token 만료됨 : {}", e.getMessage());
      throw new JwtException("만료된 토큰입니다.", e);
    } catch (SignatureException e) {
      log.error("JWT 서명 검증 실패 : {}", e.getMessage());
      throw new JwtException("유효하지 않은 서명입니다.", e);
    } catch (JwtException e) {
      log.error("JWT 검증 실패 : {}", e.getMessage());
      throw new JwtException("유효하지 않은 토큰입니다.", e);
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