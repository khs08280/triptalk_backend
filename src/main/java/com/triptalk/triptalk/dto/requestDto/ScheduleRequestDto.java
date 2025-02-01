package com.triptalk.triptalk.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ScheduleRequestDto {
  @NotNull(message = "tripId는 필수 값입니다.")
  private Long tripId;

  @NotNull(message = "날짜(date)는 필수 값입니다.")
  private LocalDate date;

  @NotBlank(message = "장소(place)는 비어 있을 수 없습니다.")
  @Size(max = 255, message = "place는 최대 255자 이하여야 합니다.")
  private String place;

  private LocalTime startTime;
  private LocalTime endTime;

  @Size(max = 1000, message = "메모(memo)는 최대 1000자 이하여야 합니다.")
  private String memo;
}
