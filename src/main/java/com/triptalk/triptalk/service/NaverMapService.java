package com.triptalk.triptalk.service;

import com.triptalk.triptalk.dto.responseDto.NaverPlaceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

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

  public NaverPlaceResponseDto searchPlaces(String query, Integer display, Integer start) {
    if (query == null || query.trim().isEmpty()) {
      return new NaverPlaceResponseDto(); // 빈 결과 반환 또는 예외 발생
      // throw new IllegalArgumentException("Query cannot be empty."); // 예외를 발생시킬 수도 있음
    }
    log.info("query:{}, display:{},start:{}", query, display, start);

    try {
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
              .block(); // 동기 처리 (React Query에서 사용)
    } catch (WebClientResponseException e) {
      log.error("Naver API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
      // 여기서 적절한 예외를 던지거나, null을 반환하거나, 빈 NaverPlaceResponseDto를 반환합니다.
      throw new RuntimeException("Naver API request failed: " + e.getMessage(), e); // 예외 다시 던지기

    } catch (Exception e) {
      log.error("Unexpected error during Naver API call", e);
      // 기타 예외처리.
      throw new RuntimeException("Unexpected error during Naver API call: "+ e.getMessage(), e);
    }
  }
}
