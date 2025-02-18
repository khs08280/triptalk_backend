package com.triptalk.triptalk.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetMoreMessagesRequestDto {
  private Long roomId;
  private String oldestMessageId; // nullable
}