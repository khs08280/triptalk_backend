package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.TripRequestDto;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.TripResponseDto;
import com.triptalk.triptalk.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@Slf4j
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @PostMapping
  public ResponseEntity<ApiResponse<TripResponseDto>> createTrip(@Valid @RequestBody TripRequestDto tripRequestDto, @AuthenticationPrincipal User user) {
    log.info("여행 계획 생성 요청: userId={}, title={}", user.getId(), tripRequestDto.getTitle());

    TripResponseDto response = tripService.saveTrip(tripRequestDto, user);

    log.info("여행 계획 생성 완료: tripId={}", response.getId());
    return ResponseEntity.ok(ApiResponse.success("여행 계획이 생성되었습니다.", response));
  }

  @GetMapping("/{tripId}")
  public ResponseEntity<ApiResponse<TripResponseDto>> getTrip(@PathVariable Long tripId) {
    log.info("여행 조회 요청: tripId={}", tripId);

    TripResponseDto tripResponseDto = tripService.getTripById(tripId);

    log.info("여행 조회 완료: tripId={}", tripId);
    return ResponseEntity.ok(ApiResponse.success("여행 조회 성공", tripResponseDto));
  }

  @PatchMapping("/{tripId}")
  public ResponseEntity<ApiResponse<TripResponseDto>> updateTrip(@PathVariable Long tripId,
                                                                 @RequestBody TripRequestDto tripRequestDto,
                                                                 @AuthenticationPrincipal User user) {
    log.info("여행 수정 요청: tripId={}, userId={}, title={}", tripId, user.getId(), tripRequestDto.getTitle());

    TripResponseDto tripResponseDto = tripService.modifyTrip(tripId, tripRequestDto, user.getId());

    log.info("여행 수정 완료: tripId={}", tripResponseDto.getId());
    return ResponseEntity.ok(ApiResponse.success("여행 수정 성공", tripResponseDto));
  }

  @DeleteMapping("/{tripId}")
  public ResponseEntity<ApiResponse<String>> deleteTrip(@PathVariable Long tripId,
                                                        @AuthenticationPrincipal User user) {
    log.info("여행 삭제 요청: tripId={}, userId={}", tripId, user.getId());

    String message = tripService.removeTripById(tripId, user.getId());

    log.info("여행 삭제 완료: tripId={}, message={}", tripId, message);
    return ResponseEntity.ok(ApiResponse.success(message, null));
  }
}