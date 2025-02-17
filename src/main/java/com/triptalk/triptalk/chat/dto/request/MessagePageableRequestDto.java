package com.triptalk.triptalk.chat.dto.request;

import lombok.Getter;

@Getter
public class MessagePageableRequestDto {
  public Long roomId;
  public Long userId;
  public int page;
}
