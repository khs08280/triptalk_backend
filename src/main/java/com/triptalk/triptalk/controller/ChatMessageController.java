package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.chat.dto.MessagesResponseDto;
import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.ChatRoomResponseDto;
import com.triptalk.triptalk.service.ChatMessageService;
import com.triptalk.triptalk.service.ChatRoomUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatMessages")
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

  private final ChatRoomUserService chatRoomUserService;
  private final ChatMessageService chatMessageService;
//
//  @GetMapping("/{roomId}")
//  public ResponseEntity<MessagesResponseDto> getMoreMessages(
//          @AuthenticationPrincipal User user,
//          @RequestParam("page") int page,
//          @PathVariable Long roomId) {
//
//    boolean isExistingUser = chatRoomUserService.isUserInRoom(user.getId(), roomId);
//    MessagesResponseDto messages = chatMessageService.getMoreMessages(roomId, page, 50);
//
//    return ResponseEntity.ok(messages);
//  }
}
