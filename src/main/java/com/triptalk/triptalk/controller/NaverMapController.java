package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.responseDto.NaverPlaceResponseDto;
import com.triptalk.triptalk.service.NaverMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
public class NaverMapController {

  private final NaverMapService naverMapService;

  @GetMapping("/naver")
  public NaverPlaceResponseDto searchPlaces(
          @RequestParam String query,
          @RequestParam(defaultValue = "5") Integer display,
          @RequestParam(defaultValue = "1") Integer start) {
    log.info("query:{}, display:{},start:{}", query, display, start);
    return naverMapService.searchPlaces(query, display, start);
  }
}