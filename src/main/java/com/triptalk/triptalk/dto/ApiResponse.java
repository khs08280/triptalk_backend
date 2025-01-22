package com.triptalk.triptalk.dto;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;

  // 생성자
  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  // 정적 팩토리 메서드 (static factory method)
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  public static <T> ApiResponse<T> error( String message) {
    return new ApiResponse<>(false, message, null);
  }
}