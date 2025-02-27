package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  private final String SECRET_KEY = "testSecretKeytestSecretKeytestSecretKeytestSecretKeytestSecretKey";
  private final Long ACCESS_TOKEN_EXPIRATION = 3600000L;
  private final Long REFRESH_TOKEN_EXPIRATION = 3600000L;

  @BeforeEach
  void setUp() {
    // ReflectionTestUtils를 사용하여 private 필드에 값 주입
    ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
    ReflectionTestUtils.setField(jwtService, "ACCESS_TOKEN_EXPIRATION", ACCESS_TOKEN_EXPIRATION);
    ReflectionTestUtils.setField(jwtService, "REFRESH_TOKEN_EXPIRATION", REFRESH_TOKEN_EXPIRATION);
  }


  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String createTestToken(String username, Date expiration) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", 1L); // 예시: userId 클레임 추가

    return io.jsonwebtoken.Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expiration)
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
  }


  @Test
  @DisplayName("username 추출 성공")
  void extractUsername_Success() {
    // Given
    String expectedUsername = "testuser";
    String token = createTestToken(expectedUsername, new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));

    // When
    String actualUsername = jwtService.extractUsername(token);

    // Then
    assertThat(actualUsername).isEqualTo(expectedUsername);
  }

  @Test
  @DisplayName("만료시간 추출 성공")
  void extractExpiration_Success() {
    // Given
    Date expectedExpiration = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION);
    String token = createTestToken("testuser", expectedExpiration);

    // When
    Date actualExpiration = jwtService.extractExpiration(token);

    // Then
    assertThat(actualExpiration).isCloseTo(expectedExpiration, 1000); // 1초 이내 오차 허용
  }


  @Test
  @DisplayName("토큰 생성 성공")
  void generateToken_Success() {
    // Given
    String username = "testUser";
    User mockUser = User.builder().id(1L).username(username).build();
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

    // When
    String token = jwtService.generateAccessToken(username);

    // Then
    assertThat(token).isNotNull();
    assertThat(jwtService.extractUsername(token)).isEqualTo(username);
    assertThat((Long) jwtService.extractClaim(token, claims -> claims.get("userId", Long.class))).isEqualTo(1L);
  }


  @Test
  @DisplayName("유효한 토큰 검증 성공")
  void isTokenValid_ValidToken_ReturnsTrue() {
    // Given
    String username = "testuser";
    String token = createTestToken(username, new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(username)
            .password("password")
            .roles("USER")
            .build();

    // When
    Boolean isValid = jwtService.isTokenValid(token, userDetails);

    // Then
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("만료된 토큰 검증 실패")
  void isTokenValid_ExpiredToken_ReturnsFalse() {
    // Given
    String username = "testuser";
    String token = createTestToken(username, new Date(System.currentTimeMillis() - 10000)); // 과거 시간으로 만료
    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(username)
            .password("password")
            .roles("USER")
            .build();

    // When
    Boolean isValid = jwtService.isTokenValid(token, userDetails);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("잘못된 사용자 이름으로 토큰 검증 실패")
  void isTokenValid_IncorrectUsername_ReturnsFalse() {
    // Given
    String token = createTestToken("testuser1", new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username("testuser2") // 다른 사용자 이름
            .password("password")
            .roles("USER")
            .build();

    // When
    Boolean isValid = jwtService.isTokenValid(token, userDetails);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("validateToken 성공")
  void validateToken_Success() {
    // Given
    String token = createTestToken("testuser", new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));

    // When & Then (예외가 발생하지 않으면 성공)
    jwtService.validateToken(token);
  }


  @Test
  @DisplayName("만료된 토큰으로 validateToken 호출 시 예외 발생")
  void validateToken_ExpiredToken_ThrowsExpiredJwtException() {
    // Given
    String token = createTestToken("testUser", new Date(System.currentTimeMillis() - 10000));

    // When & Then
    assertThatThrownBy(() -> jwtService.validateToken(token))
            .isInstanceOf(JwtException.class)
            .hasMessageContaining("만료된 토큰입니다.");
  }

  @Test
  @DisplayName("잘못된 서명으로 validateToken 호출 시 예외 발생")
  void validateToken_InvalidSignature_ThrowsSignatureException() {
    // Given
    String invalidToken = "invalidToken";  // 유효하지 않은 토큰 (서명 검증 실패)

    // When & Then
    assertThatThrownBy(() -> jwtService.validateToken(invalidToken))
            .isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("잘못된 토큰 구조로 validateToken 호출 시 예외 발생")
  void validateToken_InvalidTokenStructure() {
    // Given
    String invalidToken = "invalid.token.structure";

    // When & Then
    assertThatThrownBy(() -> jwtService.validateToken(invalidToken))
            .isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("userId 추출 성공")
  void getUserId_Success() {
    // Given
    Long expectedUserId = 123L;
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", expectedUserId);
    String token = createTestTokenWithClaims(claims, "testuser", new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));

    // When
    Long actualUserId = jwtService.getUserId(token);

    // Then
    assertThat(actualUserId).isEqualTo(expectedUserId);
  }
  private String createTestTokenWithClaims(Map<String, Object> claims, String username, Date expiration) {
    return io.jsonwebtoken.Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expiration)
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
  }


  @Test
  @DisplayName("만료된 토큰에서 userId 추출 시 예외 발생")
  void getUserId_ExpiredToken_ThrowsExpiredJwtException() {
    // Given
    String token = createTestToken("testUser", new Date(System.currentTimeMillis() - 10000)); // 만료된 토큰

    // When & Then
    assertThatThrownBy(() -> jwtService.getUserId(token))
            .isInstanceOf(JwtException.class)
            .hasMessageContaining("만료된 토큰입니다.");
  }
}