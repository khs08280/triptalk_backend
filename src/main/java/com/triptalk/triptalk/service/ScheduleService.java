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

  /**
   * 스케줄 생성
   */
  public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
    // 1) Trip 엔티티 확인
    Trip trip = tripRepository.findById(requestDto.getTripId())
            .orElseThrow(() -> new IllegalArgumentException("해당 tripId가 존재하지 않습니다. id=" + requestDto.getTripId()));

    // 2) Schedule 엔티티 생성
    Schedule schedule = new Schedule(
            null,                   // id(auto generated)
            trip,                   // @ManyToOne Trip
            requestDto.getDate(),
            requestDto.getPlace(),
            requestDto.getStartTime(),
            requestDto.getEndTime(),
            requestDto.getMemo(),
            null,                   // createdAt(Auditing)
            null                    // updatedAt(Auditing)
    );

    // 3) DB 저장
    Schedule saved = scheduleRepository.save(schedule);

    // 4) 엔티티 -> DTO 변환하여 반환
    return ScheduleResponseDto.from(saved);
  }

  /**
   * 특정 스케줄 조회
   */
  @Transactional(readOnly = true)
  public ScheduleResponseDto getSchedule(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다. id=" + scheduleId));

    return ScheduleResponseDto.from(schedule);
  }

  /**
   * 모든 스케줄 조회
   */
  @Transactional(readOnly = true)
  public List<ScheduleResponseDto> getAllSchedules() {
    List<Schedule> schedules = scheduleRepository.findAll();
    return schedules.stream()
            .map(ScheduleResponseDto::from)
            .toList();
  }

  /**
   * 스케줄 수정
   */
  public ScheduleResponseDto updateSchedule(Long scheduleId, ScheduleRequestDto requestDto) {
    // 1) 기존 스케줄 조회
    Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다. id=" + scheduleId));

    // 2) Trip 변경 필요 시
    if (!schedule.getTrip().getId().equals(requestDto.getTripId())) {
      Trip newTrip = tripRepository.findById(requestDto.getTripId())
              .orElseThrow(() -> new IllegalArgumentException("해당 tripId가 존재하지 않습니다. id=" + requestDto.getTripId()));

      // 편의상 "새 엔티티를 재생성"하는 방식 예시 (setter가 없는 경우)
      schedule = new Schedule(
              schedule.getId(),
              newTrip,
              requestDto.getDate(),
              requestDto.getPlace(),
              requestDto.getStartTime(),
              requestDto.getEndTime(),
              requestDto.getMemo(),
              schedule.getCreatedAt(),  // 기존 생성일시
              schedule.getUpdatedAt()   // 기존 수정일시
      );
    } else {
      // Trip이 바뀌지 않는다면 나머지 필드만 변경 (마찬가지로 재생성 예시)
      schedule = new Schedule(
              schedule.getId(),
              schedule.getTrip(),
              requestDto.getDate(),
              requestDto.getPlace(),
              requestDto.getStartTime(),
              requestDto.getEndTime(),
              requestDto.getMemo(),
              schedule.getCreatedAt(),
              schedule.getUpdatedAt()
      );
    }

    // 3) DB 저장
    Schedule updated = scheduleRepository.save(schedule);

    // 4) 엔티티 -> DTO 변환
    return ScheduleResponseDto.from(updated);
  }

  /**
   * 스케줄 삭제
   */
  public void deleteSchedule(Long scheduleId) {
    if (!scheduleRepository.existsById(scheduleId)) {
      throw new IllegalArgumentException("삭제할 스케줄이 존재하지 않습니다. id=" + scheduleId);
    }
    scheduleRepository.deleteById(scheduleId);
  }
}