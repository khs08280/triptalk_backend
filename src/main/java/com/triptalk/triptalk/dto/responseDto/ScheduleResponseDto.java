package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponseDto {

  private Long id;
  private Long tripId;
  private LocalDate date;
  private String name;
  private PlaceResponseDto placeResponseDto;
  private LocalTime startTime;
  private LocalTime endTime;
  private String memo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ScheduleResponseDto fromEntity(Schedule schedule) {
    return ScheduleResponseDto.builder()
            .id(schedule.getId())
            .tripId(schedule.getTrip().getId())
            .date(schedule.getDate())
            .name(schedule.getName())
            .placeResponseDto(Optional.ofNullable(schedule.getPlace()).map(PlaceResponseDto::fromEntity).orElse(null))
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .memo(schedule.getMemo())
            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
  }
}