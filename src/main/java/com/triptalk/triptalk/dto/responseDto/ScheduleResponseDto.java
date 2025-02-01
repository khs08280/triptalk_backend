package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponseDto {

  private Long id;
  private Long tripId;            // Schedule과 연관된 Trip의 ID만 노출
  private LocalDate date;
  private String place;
  private LocalTime startTime;
  private LocalTime endTime;
  private String memo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * Schedule 엔티티 -> DTO 변환 메서드
   */
  public static ScheduleResponseDto from(Schedule schedule) {
    return ScheduleResponseDto.builder()
            .id(schedule.getId())
            .tripId(schedule.getTrip().getId())
            .date(schedule.getDate())
            .place(schedule.getPlace())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .memo(schedule.getMemo())
            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
  }
}