package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Place;
import com.triptalk.triptalk.domain.entity.Schedule;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.enums.PlaceType;
import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import com.triptalk.triptalk.dto.responseDto.ScheduleResponseDto;
import com.triptalk.triptalk.exception.BadRequestException;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.PlaceRepository;
import com.triptalk.triptalk.repository.ScheduleRepository;
import com.triptalk.triptalk.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final TripRepository tripRepository;
  private final PlaceService placeService;

  @Transactional
  public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleDto) {
    Trip trip = tripRepository.findById(scheduleDto.getTripId())
            .orElseThrow(() -> new ResourceNotFoundException("해당 여행을 찾을 수 없습니다. id: " + scheduleDto.getTripId()));

    Schedule schedule = Schedule.builder()
            .trip(trip)
            .date(scheduleDto.getDate())
            .startTime(scheduleDto.getStartTime())
            .endTime(scheduleDto.getEndTime())
            .memo(scheduleDto.getMemo())
            .build();

    if (scheduleDto.getPlaceType() == PlaceType.CUSTOM) {
      // CUSTOM 타입이면, placeName만 설정하고 Place는 null로 둡니다.
      schedule.setName(scheduleDto.getName());
    } else if (scheduleDto.getPlaceType() == PlaceType.NAVER || scheduleDto.getPlaceType() == PlaceType.GOOGLE) {
      // NAVER 또는 GOOGLE 타입이면, PlaceService를 사용하여 Place 정보 가져오기/생성
      Place place = placeService.getOrCreatePlace(scheduleDto);
      schedule.setPlace(place);
      schedule.setName(place.getName());
    } else {
      throw new BadRequestException("잘못된 장소 타입입니다.");
    }
    Schedule save = scheduleRepository.save(schedule);
    log.info("저장되는 객체:{}", save.getName());
    return ScheduleResponseDto.fromEntity(save);
  }

  @Transactional(readOnly = true)
  public ScheduleResponseDto getSchedule(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 스케줄을 찾을 수 없습니다. id=" + scheduleId));

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
            .orElseThrow(() -> new ResourceNotFoundException("해당 스케줄을 찾을 수 없습니다. id=" + scheduleId));

    schedule = schedule.updateDetails(requestDto);

    return ScheduleResponseDto.fromEntity(schedule);
  }

  public void deleteSchedule(Long scheduleId) {
    if (!scheduleRepository.existsById(scheduleId)) {
      throw new ResourceNotFoundException("해당 스케줄이 존재하지 않습니다. id=" + scheduleId);
    }
    scheduleRepository.deleteById(scheduleId);
  }
}