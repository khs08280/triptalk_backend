package com.triptalk.triptalk.chat.handler;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.triptalk.triptalk.chat.dto.MessagesResponseDto;
import com.triptalk.triptalk.chat.dto.request.ChatMessageRequestDto;
import com.triptalk.triptalk.chat.dto.request.JoinRoomRequestDto;
import com.triptalk.triptalk.chat.dto.request.MessagePageableRequestDto;
import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import com.triptalk.triptalk.domain.entity.ChatMessage;
import com.triptalk.triptalk.service.ChatMessageService;
import com.triptalk.triptalk.service.ChatRoomUserService;
import com.triptalk.triptalk.service.JwtService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOHandler {

  private final SocketIOServer socketIOServer;
  private final ChatRoomUserService chatRoomUserService;
  private final ChatMessageService chatMessageService;
  private final JwtService jwtService;


  @PostConstruct
  private void init() {
    socketIOServer.addConnectListener(client -> {
      HandshakeData handshakeData = client.getHandshakeData();

      String[] cookieHeaders = handshakeData.getHttpHeaders().get("cookie").split(";");
      String token = null;
      for (String header : cookieHeaders) {
        String[] cookies = header.split(";");
        for (String cookie : cookies) {
          String[] pair = cookie.trim().split("=");
          if (pair.length == 2 && "accessToken".equals(pair[0])) {
            token = pair[1];
            break;
          }
        }
      }
      log.info("accessToken: {}", token);

      if (token == null || token.isEmpty()) {
        log.warn("⛔ 토큰이 전송되지 않음 - 소켓 연결 거부");
        client.disconnect();
        return;
      }

      try {
        jwtService.validateToken(token); // 유효하지 않으면 예외 발생

        Long userId = jwtService.getUserId(token);

        // 소켓 client 객체에 "userId"라는 키로 보관
        client.set("userId", userId);
        log.info("🔵 유저 연결 성공: sessionId={}, userId={}", client.getSessionId(), userId);

        // 연결 성공 메시지 보내기 (옵션)
        client.sendEvent("connect_success", "서버에 인증된 연결이 완료되었습니다.");

      } catch (Exception e) {
        log.error("⛔ JWT 토큰 검증 실패: {}", e.getMessage(), e);
        client.disconnect(); // 인증 실패 시 즉시 연결 해제
      }
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
        List<ChatMessageResponseDto> lastMessages = chatMessageService.getLastMessages(data.getRoomId());
        client.sendEvent("load_old_messages", lastMessages);
      } else {
        log.info("새로운 유저 {}가 채팅방 {}에 참여", data.getUserId(), data.getRoomId());
        String joinMessage = data.getUserId() + "님이 채팅방에 참여했습니다.";
        socketIOServer.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent("user_joined", joinMessage);

        chatRoomUserService.addUserToRoom(data.getUserId(), data.getRoomId());

//        List<ChatMessageResponseDto> lastMessages = chatMessageService.getLastMessages(data.getRoomId(), 50);
//        client.sendEvent("load_old_messages", lastMessages);
      }
    });

    socketIOServer.addEventListener("get_more_messages", MessagePageableRequestDto.class, (client, data, ackRequest) -> {
      client.joinRoom(String.valueOf(data.getRoomId()));
      boolean isExistingUser = chatRoomUserService.isUserInRoom(data.getUserId(), data.getRoomId());

      MessagesResponseDto lastMessages = chatMessageService.getMoreMessages(data.getRoomId(), data.getPage(), 50);
      client.sendEvent("get_more_messages", lastMessages);
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