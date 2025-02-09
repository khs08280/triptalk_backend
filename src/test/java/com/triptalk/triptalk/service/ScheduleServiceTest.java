package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Place;
import com.triptalk.triptalk.domain.entity.Schedule;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.enums.PlaceType;
import com.triptalk.triptalk.dto.requestDto.PlaceRequestDto;
import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import com.triptalk.triptalk.dto.responseDto.ScheduleResponseDto;
import com.triptalk.triptalk.exception.BadRequestException;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ScheduleRepository;
import com.triptalk.triptalk.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

  @InjectMocks
  private ScheduleService scheduleService;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private PlaceService placeService;

  @Test
  @DisplayName("스케줄 생성 성공 - CUSTOM 타입")
  void createSchedule_Custom_Success() {
    // Given

    PlaceRequestDto placeDto = PlaceRequestDto.builder().build();
    Long tripId = 1L;
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .tripId(tripId)
            .name("Custom Place")
            .placeType(PlaceType.CUSTOM)
            .date(LocalDate.of(2023, 1, 1))
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(12, 0))
            .place(placeDto)
            .memo("Custom Memo")
            .build();

    Trip mockTrip = Trip.builder().build();
    ReflectionTestUtils.setField(mockTrip, "id", tripId);

    Schedule scheduleToSave = Schedule.builder()
            .trip(mockTrip)
            .name(requestDto.getName()) // CUSTOM 타입은 placeName 사용
            .date(requestDto.getDate())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .build();

    Schedule savedSchedule = Schedule.builder()
            .trip(mockTrip)
            .name(requestDto.getName())
            .date(requestDto.getDate())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .build();

    ReflectionTestUtils.setField(savedSchedule, "id", 1L); // Set id

    when(tripRepository.findById(tripId)).thenReturn(Optional.of(mockTrip));
    when(scheduleRepository.save(any(Schedule.class))).thenReturn(savedSchedule);


    // When
    ScheduleResponseDto result = scheduleService.createSchedule(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L); // Assuming savedSchedule has id 1L
    assertThat(result.getName()).isEqualTo("Custom Place"); // CUSTOM 타입은 placeName 사용
    assertThat(result.getPlaceResponseDto()).isNull(); // CUSTOM 타입은 Place가 null
    verify(tripRepository, times(1)).findById(tripId);
    verify(scheduleRepository, times(1)).save(any(Schedule.class));
    verify(placeService, never()).getOrCreatePlace(any()); // PlaceService 호출 안됨

  }

  @Test
  @DisplayName("스케줄 생성 성공 - NAVER 타입")
  void createSchedule_Naver_Success() {
    // Given
    Long tripId = 1L;
    Long placeId = 2L;
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .tripId(tripId)
            .name("Naver Place")
            .placeType(PlaceType.NAVER)
            .placeId(placeId) // NAVER 타입에는 placeId 필요
            .date(LocalDate.of(2023, 1, 1))
            .startTime(LocalTime.of(14, 0))
            .endTime(LocalTime.of(16, 0))
            .memo("Naver Memo")
            .build();


    Trip mockTrip = Trip.builder().build();
    ReflectionTestUtils.setField(mockTrip, "id", tripId);
    Place mockPlace = Place.builder().name("Naver Place").build();
    ReflectionTestUtils.setField(mockPlace, "id", placeId);

    Schedule scheduleToSave = Schedule.builder()
            .trip(mockTrip)
            .place(mockPlace)
            .name(mockPlace.getName()) // place의 이름을 사용
            .date(requestDto.getDate())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .build();

    Schedule savedSchedule = Schedule.builder()
            .trip(mockTrip)
            .place(mockPlace)
            .name(mockPlace.getName())
            .date(requestDto.getDate())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .build();

    ReflectionTestUtils.setField(savedSchedule, "id", 1L); // Set id

    when(tripRepository.findById(tripId)).thenReturn(Optional.of(mockTrip));
    when(placeService.getOrCreatePlace(requestDto)).thenReturn(mockPlace);
    when(scheduleRepository.save(any(Schedule.class))).thenReturn(savedSchedule);

    // When
    ScheduleResponseDto result = scheduleService.createSchedule(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L); // Assuming savedSchedule has id 1
    assertThat(result.getPlaceResponseDto().getName()).isEqualTo("Naver Place"); // Place 이름 사용
    assertThat(result.getPlaceResponseDto().getId()).isEqualTo(placeId);
    verify(tripRepository).findById(tripId);
    verify(placeService).getOrCreatePlace(requestDto);
    verify(scheduleRepository).save(any(Schedule.class));
  }

  @Test
  @DisplayName("스케줄 생성 실패 - 잘못된 PlaceType")
  void createSchedule_InvalidPlaceType_ThrowsException() {
    // Given
    Long tripId = 1L;
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .tripId(tripId)
            .name("Invalid Place")
            .placeType(null) // Invalid PlaceType
            .date(LocalDate.now())
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusHours(2))
            .build();

    Trip mockTrip = Trip.builder().build();
    ReflectionTestUtils.setField(mockTrip, "id", tripId);


    when(tripRepository.findById(tripId)).thenReturn(Optional.of(mockTrip));

    // When & Then
    assertThatThrownBy(() -> scheduleService.createSchedule(requestDto))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("잘못된 장소 타입");
    verify(tripRepository, times(1)).findById(tripId); // tripRepository는 호출되어야 함.
    verify(scheduleRepository, never()).save(any(Schedule.class)); // save()는 호출되지 않아야 함.
    verify(placeService, never()).getOrCreatePlace(any());
  }
  @Test
  @DisplayName("스케줄 생성 실패 - Trip 없음")
  void createSchedule_TripNotFound_ThrowsException() {
    // Given
    Long nonExistentTripId = 999L;
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .tripId(nonExistentTripId)
            .name("Some Place")
            .placeType(PlaceType.CUSTOM)
            .date(LocalDate.now())
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusHours(2))
            .build();


    when(tripRepository.findById(nonExistentTripId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> scheduleService.createSchedule(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 여행을 찾을 수 없습니다.");

    verify(tripRepository, times(1)).findById(nonExistentTripId);
    verify(scheduleRepository, never()).save(any(Schedule.class)); // save()는 호출되지 않아야 함.
  }

  @Test
  @DisplayName("스케줄 조회 성공")
  void getSchedule_Success() {
    // Given
    Long scheduleId = 1L;
    Trip trip = Trip.builder().build();
    ReflectionTestUtils.setField(trip, "id", 1L);
    Schedule mockSchedule = Schedule.builder()
            .trip(trip)
            .name("Test Schedule")
            .date(LocalDate.now())
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusHours(1))
            .build();
    ReflectionTestUtils.setField(mockSchedule, "id", scheduleId);
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));

    // When
    ScheduleResponseDto result = scheduleService.getSchedule(scheduleId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(scheduleId);
    assertThat(result.getName()).isEqualTo("Test Schedule");
    verify(scheduleRepository).findById(scheduleId);
  }

  @Test
  @DisplayName("스케줄 조회 실패 - Schedule 없음")
  void getSchedule_NotFound_ThrowsException() {
    // Given
    Long nonExistentScheduleId = 999L;
    when(scheduleRepository.findById(nonExistentScheduleId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> scheduleService.getSchedule(nonExistentScheduleId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 스케줄을 찾을 수 없습니다.");
    verify(scheduleRepository, times(1)).findById(nonExistentScheduleId);
  }

  @Test
  @DisplayName("모든 스케줄 조회 성공")
  void getAllSchedules_Success() {
    // Given
    Trip trip = Trip.builder().build();
    ReflectionTestUtils.setField(trip, "id", 1L);
    Schedule schedule1 = Schedule.builder().trip(trip).name("Schedule 1").build();
    Schedule schedule2 = Schedule.builder().trip(trip).name("Schedule 2").build();
    ReflectionTestUtils.setField(schedule1, "id", 1L);
    ReflectionTestUtils.setField(schedule2, "id", 2L);

    List<Schedule> schedules = Arrays.asList(schedule1, schedule2);
    when(scheduleRepository.findAll()).thenReturn(schedules);

    // When
    List<ScheduleResponseDto> result = scheduleService.getAllSchedules();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Schedule 1");
    assertThat(result.get(1).getName()).isEqualTo("Schedule 2");
    verify(scheduleRepository, times(1)).findAll();
  }
  @Test
  @DisplayName("스케줄 업데이트 성공")
  void updateSchedule_Success() {
    // Given
    Long scheduleId = 1L;
    Trip trip = Trip.builder().build();
    ReflectionTestUtils.setField(trip, "id", 1L);
    Schedule existingSchedule = Schedule.builder()
            .trip(trip)
            .name("Old Name")
            .date(LocalDate.of(2023,1,1))
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11,0))
            .memo("Old Memo")
            .build();
    ReflectionTestUtils.setField(existingSchedule, "id", scheduleId);


    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .tripId(1L)
            .name("New Name") // Updated name
            .date(LocalDate.of(2024, 1, 1))
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13,0))
            .memo("New Memo")
            .build();

    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));

    // When
    ScheduleResponseDto result = scheduleService.updateSchedule(scheduleId, requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(scheduleId);
    assertThat(result.getName()).isEqualTo("New Name");
    assertThat(result.getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(result.getStartTime()).isEqualTo(LocalTime.of(12, 0));
    assertThat(result.getEndTime()).isEqualTo(LocalTime.of(13,0));
    assertThat(result.getMemo()).isEqualTo("New Memo");

    verify(scheduleRepository, times(1)).findById(scheduleId);
  }

  @Test
  @DisplayName("스케줄 업데이트 실패 - Schedule 없음")
  void updateSchedule_ScheduleNotFound_ThrowsException() {
    // Given
    Long nonExistentScheduleId = 999L;
    ScheduleRequestDto requestDto = new ScheduleRequestDto(); // Doesn't matter for this test

    when(scheduleRepository.findById(nonExistentScheduleId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> scheduleService.updateSchedule(nonExistentScheduleId, requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 스케줄을 찾을 수 없습니다.");
    verify(scheduleRepository).findById(nonExistentScheduleId);
  }

  @Test
  @DisplayName("스케줄 삭제 성공")
  void deleteSchedule_Success() {
    // Given
    Long scheduleId = 1L;
    when(scheduleRepository.existsById(scheduleId)).thenReturn(true);
    doNothing().when(scheduleRepository).deleteById(scheduleId);

    // When
    scheduleService.deleteSchedule(scheduleId);

    // Then
    verify(scheduleRepository).existsById(scheduleId);
    verify(scheduleRepository).deleteById(scheduleId);
  }

  @Test
  @DisplayName("스케줄 삭제 실패 - Schedule 없음")
  void deleteSchedule_NotFound_ThrowsException() {
    // Given
    Long nonExistentScheduleId = 999L;
    when(scheduleRepository.existsById(nonExistentScheduleId)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> scheduleService.deleteSchedule(nonExistentScheduleId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 스케줄이 존재하지 않습니다.");
    verify(scheduleRepository, times(1)).existsById(nonExistentScheduleId);
    verify(scheduleRepository, never()).deleteById(nonExistentScheduleId);
  }
}