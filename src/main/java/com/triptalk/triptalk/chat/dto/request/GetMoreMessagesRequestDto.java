package com.triptalk.triptalk.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetMoreMessagesRequestDto {
  private Long roomId;
  private Long oldestMessageId;
  private int size;
}