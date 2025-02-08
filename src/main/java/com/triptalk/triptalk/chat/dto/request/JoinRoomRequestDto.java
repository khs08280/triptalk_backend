package com.triptalk.triptalk.chat.dto.request;

import lombok.Getter;

@Getter
public class JoinRoomRequestDto {
  private Long roomId;
  private Long userId;
}
