package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.responseDto.NaverPlaceResponseDto;
import com.triptalk.triptalk.service.NaverMapService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/naver")
@RequiredArgsConstructor
public class NaverMapController {

  private final NaverMapService naverMapService;

  @GetMapping("/search")
  public NaverPlaceResponseDto searchPlaces(
          @RequestParam String query,
          @RequestParam(defaultValue = "5") int display,
          @RequestParam(defaultValue = "1") int start) {
    return naverMapService.searchPlaces(query, display, start);
  }
}