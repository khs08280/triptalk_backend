package com.triptalk.triptalk.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.triptalk.triptalk.domain.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponseDto {
  private Long id;
  private Long roomId;
  private Long senderId;
  private String message;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonProperty("sentAt")
  private LocalDateTime sentAt;

  public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
    return ChatMessageResponseDto.builder()
            .id(chatMessage.getId())
            .roomId(chatMessage.getChatRoom().getId())
            .senderId(chatMessage.getSender().getId())
            .message(chatMessage.getMessage())
            .sentAt(chatMessage.getSentAt())
            .build();
  }

}