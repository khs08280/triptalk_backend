package com.triptalk.triptalk.exception;

import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 404 Not Found
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException e) {
    log.error("해당 정보를 찾을 수 없습니다. : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getMessage()));
  }

  // 400 Bad Request
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException e) {
    log.error("잘못된 요청 : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getMessage()));
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ApiResponse<?>> handleInvalidTokenException(InvalidTokenException e) {
    log.error("쿠키가 없거나 오류가 발생 : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(e.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException e) {
    log.error("잘못된 요청 : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getMessage()));
  }

  // 403 Forbidden
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<ApiResponse<?>> handleSecurityException(SecurityException e) {
    log.error("권한이 없음 : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(e.getMessage()));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ApiResponse<?>> handleExpiredJwtException(ExpiredJwtException e) {
    log.error("JWT 토큰이 만료되었습니다. : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(e.getMessage()));
  }
  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException e) {
    log.error("유효하지 않은 JWT 토큰입니다. : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(e.getMessage()));
  }
  // 500 Internal Server Error (기타 모든 예외)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception e) {
    log.error("예상치 못한 예외 발생 : {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("서버에서 오류가 발생했습니다."));
  }
}
