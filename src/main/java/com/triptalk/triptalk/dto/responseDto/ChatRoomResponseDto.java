package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
  private Long chatRoomId;
  private Long tripId;
  private String title;
  private String location;

}
