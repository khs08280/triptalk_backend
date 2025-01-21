package com.triptalk.triptalk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

  @NotBlank(message = "ID는 필수 값입니다.")
  private String username;

  @NotBlank(message = "비밀번호는 필수 값입니다.")
  private String password;
}