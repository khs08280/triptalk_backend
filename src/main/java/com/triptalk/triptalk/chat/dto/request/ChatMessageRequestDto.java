package com.triptalk.triptalk.chat.dto.request;

import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
  private Long roomId;
  private Long senderId;
  private String nickname;
  private String message;
}
