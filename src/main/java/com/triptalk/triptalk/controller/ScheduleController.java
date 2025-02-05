package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.ScheduleResponseDto;
import com.triptalk.triptalk.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

  private final ScheduleService scheduleService;

  @PostMapping
  public ResponseEntity<ApiResponse<ScheduleResponseDto>> createSchedule(
          @Valid @RequestBody ScheduleRequestDto requestDto
  ) {
    ScheduleResponseDto created = scheduleService.createSchedule(requestDto);
    // "스케줄 생성 성공" 메시지를 함께 내려주고 싶다면
    ApiResponse<ScheduleResponseDto> response = ApiResponse.success("스케줄 생성 완료",created);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{scheduleId}")
  public ResponseEntity<ApiResponse<ScheduleResponseDto>> getSchedule(@PathVariable Long scheduleId) {
    ScheduleResponseDto schedule = scheduleService.getSchedule(scheduleId);
    ApiResponse<ScheduleResponseDto> response = ApiResponse.success(schedule);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getAllSchedules() {
    List<ScheduleResponseDto> schedules = scheduleService.getAllSchedules();
    // 여러 건을 조회했으므로, List를 data로 담아서 응답
    ApiResponse<List<ScheduleResponseDto>> response = ApiResponse.success("전체 스케줄 조회 성공",schedules);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{scheduleId}")
  public ResponseEntity<ApiResponse<ScheduleResponseDto>> updateSchedule(
          @PathVariable Long scheduleId,
          @Valid @RequestBody ScheduleRequestDto requestDto
  ) {
    ScheduleResponseDto updated = scheduleService.updateSchedule(scheduleId, requestDto);
    ApiResponse<ScheduleResponseDto> response = ApiResponse.success("스케줄 수정 완료",updated);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{scheduleId}")
  public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long scheduleId) {
    scheduleService.deleteSchedule(scheduleId);
    // 삭제의 경우, 특별히 반환할 body 데이터가 없다면 Void 제네릭을 쓰고 success 처리
    ApiResponse<Void> response = new ApiResponse<>(true, "스케줄 삭제 완료", null);
    return ResponseEntity.ok(response);
  }
}