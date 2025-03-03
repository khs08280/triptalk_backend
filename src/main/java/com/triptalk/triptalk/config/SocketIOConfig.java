package com.triptalk.triptalk.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

  @Value("${socket.io.host}")
  private String host;

  @Value("${socket.io.port}")
  private int port;

  @Bean
  public SocketIOServer socketIOServer() throws Exception {
    Configuration config = new Configuration();
    config.setHostname(host);
    config.setPort(port);

    config.getAllowHeaders();
    config.setOrigin("https://triptalk1.netlify.app");

    config.setJsonSupport(new JacksonJsonSupport(new JavaTimeModule()));
    return new SocketIOServer(config);
  }
}
