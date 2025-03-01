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

  @Value("${server.ssl.key-store}")
  private String keystorePath;

  @Value("${server.ssl.key-store-password}")
  private String keystorePassword;

  @Value("${socket.io.keystore-path:/app/localhost.p12}")
  private String keystoreFilePath;

  @Bean
  public SocketIOServer socketIOServer() throws Exception {
    Configuration config = new Configuration();
    config.setHostname(host);
    config.setPort(port);

//    String path = ClassLoader.getSystemResource("localhost.p12").getPath();
//    FileInputStream fileInputStream = new FileInputStream(path);

//    org.springframework.core.io.Resource resource =
//            new org.springframework.core.io.ClassPathResource("localhost.p12");
//    File keyStoreFile = resource.getFile();
    File keyStoreFile = new File(keystoreFilePath);
    FileInputStream fileInputStream = new FileInputStream(keyStoreFile);

    config.setKeyStorePassword(keystorePassword);
    config.setKeyStore(fileInputStream);

    config.getAllowHeaders();
    config.setOrigin("https://triptalk1.netlify.app");

    config.setJsonSupport(new JacksonJsonSupport(new JavaTimeModule()));
    return new SocketIOServer(config);
  }
}
