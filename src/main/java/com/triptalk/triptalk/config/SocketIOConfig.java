package com.triptalk.triptalk.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

  @Value("${socket.io.host}")
  private String host;

  @Value("${socket.io.port}")
  private int port;

  @Bean
  public SocketIOServer socketIOServer(){
    Configuration config = new Configuration();
    config.setHostname(host);
    config.setPort(port);

    return new SocketIOServer(config);
  }
}
