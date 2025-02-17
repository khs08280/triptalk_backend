package com.triptalk.triptalk.controller;


import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.ChatRoomResponseDto;
import com.triptalk.triptalk.service.ChatRoomUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatRooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

  private final ChatRoomUserService chatRoomUserService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> getChatRoomList(@AuthenticationPrincipal User user){

    List<ChatRoomResponseDto> chatRoomList = chatRoomUserService.findChatRoomList(user);

    return ResponseEntity.ok(ApiResponse.success(chatRoomList));
  }

}
