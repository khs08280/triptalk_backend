package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

  private Long id;

  private String username;

  private String nickname;

  private String email;

  public static UserResponseDto fromEntity(User user) {
    return UserResponseDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .build();
  }
}