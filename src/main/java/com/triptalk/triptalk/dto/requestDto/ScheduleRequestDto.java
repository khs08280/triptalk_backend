package com.triptalk.triptalk.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequestDto {

  @NotNull(message = "tripId는 필수 값입니다.")
  private Long tripId;

  @NotNull(message = "날짜(date)는 필수 값입니다.")
  private LocalDate date;

  @NotBlank(message = "이름은 비어 있을 수 없습니다.")
  @Size(max = 255, message = "name은 최대 255자 이하여야 합니다.")
  private String name;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime endTime;

  @Size(max = 1000, message = "메모는 1000자 이하")
  private String memo;

}