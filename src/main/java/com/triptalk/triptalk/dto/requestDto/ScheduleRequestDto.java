package com.triptalk.triptalk.dto.requestDto;

import com.triptalk.triptalk.domain.entity.Place;
import com.triptalk.triptalk.domain.enums.PlaceType;
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

  private Long placeId; // Google Places API place_id 또는 places 테이블 place_id

  @NotBlank(message = "이름은 비어 있을 수 없습니다.")
  @Size(max = 255, message = "name은 최대 255자 이하여야 합니다.")
  private String name;

  private LocalTime startTime;

  private LocalTime endTime;

  @Size(max = 1000, message = "메모는 1000자 이하")
  private String memo;

  private PlaceRequestDto place;

  @NotNull
  private PlaceType placeType;

}