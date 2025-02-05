package com.triptalk.triptalk.chat.handler;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOHandler {

  private final SocketIOServer socketIOServer;

  @PostConstruct
  private void init() {
    socketIOServer.addConnectListener(client -> {
      log.info("SocketIO Client Connected: {}", client.getSessionId());
    });

    socketIOServer.addDisconnectListener(client -> {
      log.info("SocketIO Client Disconnected: {}", client.getSessionId());
    });

    socketIOServer.start();
  }

  @PreDestroy
  private void stop() {
    socketIOServer.stop();
  }
}