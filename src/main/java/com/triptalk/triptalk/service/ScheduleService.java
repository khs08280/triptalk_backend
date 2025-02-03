package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Schedule;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import com.triptalk.triptalk.dto.responseDto.ScheduleResponseDto;
import com.triptalk.triptalk.repository.ScheduleRepository;
import com.triptalk.triptalk.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final TripRepository tripRepository;

  public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
    // 1) Trip 엔티티 확인
    Trip trip = tripRepository.findById(requestDto.getTripId())
            .orElseThrow(() -> new IllegalArgumentException("해당 tripId가 존재하지 않습니다. id=" + requestDto.getTripId()));

    // 2) Schedule 엔티티 생성
    Schedule schedule = Schedule.builder()
            .trip(trip)
            .date(requestDto.getDate())
            .place(requestDto.getPlace())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .build();

    // 3) DB 저장
    Schedule saved = scheduleRepository.save(schedule);

    // 4) 엔티티 -> DTO 변환하여 반환
    return ScheduleResponseDto.fromEntity(saved);
  }

  @Transactional(readOnly = true)
  public ScheduleResponseDto getSchedule(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다. id=" + scheduleId));

    return ScheduleResponseDto.fromEntity(schedule);
  }

  @Transactional(readOnly = true)
  public List<ScheduleResponseDto> getAllSchedules() {
    List<Schedule> schedules = scheduleRepository.findAll();
    return schedules.stream()
            .map(ScheduleResponseDto::fromEntity)
            .toList();
  }

  public ScheduleResponseDto updateSchedule(Long scheduleId, ScheduleRequestDto requestDto) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다. id=" + scheduleId));

    schedule = schedule.updateDetails(requestDto);

    return ScheduleResponseDto.fromEntity(schedule);
  }

  public void deleteSchedule(Long scheduleId) {
    if (!scheduleRepository.existsById(scheduleId)) {
      throw new IllegalArgumentException("삭제할 스케줄이 존재하지 않습니다. id=" + scheduleId);
    }
    scheduleRepository.deleteById(scheduleId);
  }
}