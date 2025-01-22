package com.triptalk.triptalk.dto;

import com.triptalk.triptalk.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDto {

  @NotBlank(message = "ID는 필수 값입니다.")
  private String username;

  @NotBlank(message = "닉네임은 필수 값입니다.")
  private String nickname;

  @Email(message = "올바르지 않은 이메일 형식입니다.")
  @NotBlank(message = "이메일은 필수 값입니다.")
  private String email;

  public static UserDataDto fromEntity(User user) {
    return UserDataDto.builder()
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .build();
  }
}