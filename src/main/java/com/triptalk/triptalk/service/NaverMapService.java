package com.triptalk.triptalk.service;

import com.triptalk.triptalk.dto.responseDto.NaverPlaceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverMapService {

  private final WebClient webClient;

  @Value("${naver.api.client.id}")
  private String clientId;

  @Value("${naver.api.client.secret}")
  private String clientSecret;

  @Value("${naver.api.searchUrl}")
  private String searchUrl;

  public NaverPlaceResponseDto searchPlaces(String query, int display, int start) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("openapi.naver.com")
                    .path("/v1/search/local.json")
                    .queryParam("query", query)
                    .queryParam("display", display)
                    .queryParam("start", start)
                    .queryParam("sort", "random")
                    .build())
            .headers(headers -> {
              headers.set("X-Naver-Client-Id", clientId);
              headers.set("X-Naver-Client-Secret", clientSecret);
            })
            .retrieve()
            .bodyToMono(NaverPlaceResponseDto.class)
            .block(); // 동기 처리
  }
}
