package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
  private Long chatRoomId;
  private Long tripId;
  private String title;

  public ChatRoomResponseDto fromEntity(ChatRoom chatRoom,String titleData){
    return ChatRoomResponseDto.builder()
            .chatRoomId(chatRoom.getId())
            .tripId(chatRoom.getTrip().getId())
            .title(titleData)
            .build();
  }
}
