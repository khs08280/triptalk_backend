package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.TripRequestDto;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.TripResponseDto;
import com.triptalk.triptalk.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@Slf4j
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @PostMapping
  public ResponseEntity<ApiResponse<TripResponseDto>> createTrip(@RequestBody TripRequestDto tripRequestDto, @AuthenticationPrincipal User user){
    TripResponseDto response = tripService.saveTrip(tripRequestDto, user);

    return ResponseEntity.ok(ApiResponse.success("여행 계획이 생성되었습니다.", response));
  }

  @GetMapping("/{tripId}")
  public ResponseEntity<TripResponseDto> getTrip(@PathVariable Long tripId, @AuthenticationPrincipal User user) {
    TripResponseDto tripResponseDto = tripService.getTrip(tripId);

    if (user != null) {
      log.info("Authenticated user: {}", user.getUsername());
    }
    return ResponseEntity.ok(tripResponseDto);
  }
}
