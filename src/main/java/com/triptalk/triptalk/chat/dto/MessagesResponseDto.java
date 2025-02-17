package com.triptalk.triptalk.chat.dto;

import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MessagesResponseDto {
  private List<ChatMessageResponseDto> messages;
  private Integer nextPage;
}