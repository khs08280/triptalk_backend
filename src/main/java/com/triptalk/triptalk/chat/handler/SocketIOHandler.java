package com.triptalk.triptalk.chat.handler;

import com.corundumstudio.socketio.SocketIOServer;
import com.triptalk.triptalk.chat.dto.request.ChatMessageRequestDto;
import com.triptalk.triptalk.chat.dto.request.JoinRoomRequestDto;
import com.triptalk.triptalk.chat.dto.request.MessagePageableRequestDto;
import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import com.triptalk.triptalk.domain.entity.ChatMessage;
import com.triptalk.triptalk.service.ChatMessageService;
import com.triptalk.triptalk.service.ChatRoomUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOHandler {

  private final SocketIOServer socketIOServer;
  private final ChatRoomUserService chatRoomUserService;
  private final ChatMessageService chatMessageService;

  @PostConstruct
  private void init() {
    socketIOServer.addConnectListener(client -> {
      String sessionId = client.getSessionId().toString();
      log.info("🔵 유저 연결됨: {}", sessionId);
      client.sendEvent("connect_success", "서버에 연결되었습니다.");
    });

    socketIOServer.addDisconnectListener(client -> {
      String sessionId = client.getSessionId().toString();
      log.info("🔴 유저 연결 종료됨: {}", sessionId);
    });

    socketIOServer.addEventListener("join_room", JoinRoomRequestDto.class, (client, data, ackRequest) -> {
      client.joinRoom(String.valueOf(data.getRoomId()));
      log.info("유저 {}가 채팅방 {}에 입장 요청", data.getUserId(), data.getRoomId());

      boolean isExistingUser = chatRoomUserService.isUserInRoom(data.getUserId(), data.getRoomId());

      if (isExistingUser) {
        log.info("기존 유저 {}가 채팅방 {}에 재접속", data.getUserId(), data.getRoomId());
        List<ChatMessage> lastMessages = chatMessageService.getLastMessages(data.getRoomId(), 50);
        client.sendEvent("load_old_messages", lastMessages);
      } else {
        log.info("새로운 유저 {}가 채팅방 {}에 참여", data.getUserId(), data.getRoomId());
        String joinMessage = data.getUserId() + "님이 채팅방에 참여했습니다.";
        socketIOServer.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent("user_joined", joinMessage);

        chatRoomUserService.addUserToRoom(data.getUserId(), data.getRoomId());

        List<ChatMessage> lastMessages = chatMessageService.getLastMessages(data.getRoomId(), 50);
        client.sendEvent("메시지 불러오기", lastMessages);
      }
    });

    socketIOServer.addEventListener("get_more_messages", MessagePageableRequestDto.class, (client, data, ackRequest) -> {
      client.joinRoom(String.valueOf(data.getRoomId()));
      boolean isExistingUser = chatRoomUserService.isUserInRoom(data.getUserId(), data.getRoomId());

      List<ChatMessage> lastMessages = chatMessageService.getMoreMessages(data.getRoomId(), data.getPage(), data.getSize());
      client.sendEvent("메시지 불러오기", lastMessages);
    });

    socketIOServer.addEventListener("out_room", JoinRoomRequestDto.class, (client, data, ackRequest) -> {
      client.leaveRoom(String.valueOf(data.getRoomId()));
      log.info("유저 {}가 채팅방 {}에서 채팅방 목록으로 나갔거나 접속을 끊음", data.getUserId(), data.getRoomId());

    });

    socketIOServer.addEventListener("leave_room", JoinRoomRequestDto.class, (client, data, ackRequest) -> {
      Long userId = data.getUserId();
      Long roomId = data.getRoomId();

      if (!chatRoomUserService.isUserInRoom(userId, roomId)) {
        log.info("유저 {}는 이미 채팅방 {}의 멤버가 아닙니다. (중복 요청 또는 비정상 요청)", userId, roomId);

        if (ackRequest.isAckRequested()) {
          ackRequest.sendAckData("이미 채팅방을 탈퇴한 유저입니다.");
        }
        return;
      }

      client.leaveRoom(String.valueOf(roomId));

      String s = chatRoomUserService.deleteChatRoomUser(userId, roomId);

      String leaveUserMessage = userId + "님이 채팅방을 탈퇴했습니다.";
      socketIOServer.getRoomOperations(String.valueOf(roomId)).sendEvent("leave_room", leaveUserMessage);

      log.info("유저 {}가 채팅방 {}에서 탈퇴를 완료했습니다.", userId, roomId);
      if (ackRequest.isAckRequested()) {
        ackRequest.sendAckData("s");
      }
    });

    socketIOServer.addEventListener("send_message", ChatMessageRequestDto.class, (client, data, ackRequest) -> {
      Long userId = data.getSenderId();
      Long roomId = data.getRoomId();
      String message = data.getMessage();

      log.info("메시지 수신: {}", data);


      if (!chatRoomUserService.isUserInRoom(userId, roomId)) {
        log.warn("유저 {}가 속해있지 않은 방({})에 메시지를 전송하려 함", userId, roomId);

        if (ackRequest.isAckRequested()) {
          ackRequest.sendAckData("NOT_IN_ROOM");
        }
        return;
      }

      ChatMessageResponseDto savedMessage = chatMessageService.saveMessage(
              userId,
              roomId,
              message
      );

      socketIOServer
              .getRoomOperations(String.valueOf(roomId))
              .sendEvent("receive_message", savedMessage);

      log.info("채팅 전송 성공: {}", savedMessage.getMessage());
      if (ackRequest.isAckRequested()) {
        ackRequest.sendAckData("채팅 전송 성공");
      }
    });

    socketIOServer.start();
  }

  @PreDestroy
  private void stop() {
    socketIOServer.stop();
  }
}