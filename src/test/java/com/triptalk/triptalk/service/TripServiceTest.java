package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.TripUser;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.domain.enums.Visibility;
import com.triptalk.triptalk.dto.requestDto.TripRequestDto;
import com.triptalk.triptalk.dto.responseDto.TripResponseDto;
import com.triptalk.triptalk.repository.ChatRoomRepository;
import com.triptalk.triptalk.repository.ChatRoomUserRepository;
import com.triptalk.triptalk.repository.TripRepository;
import com.triptalk.triptalk.repository.TripUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

  @InjectMocks
  private TripService tripService;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private TripUserRepository tripUserRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;


  @Test
  @DisplayName("여행 생성 테스트")
  public void 여행생성테스트() {
    // given
    User creator = User.builder()
            .id(1L)
            .build();

    TripRequestDto requestDto = TripRequestDto.builder()
            .title("테스트 여행")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .location("서울")
            .visibility(Visibility.PUBLIC)
            .build();

    Trip trip = Trip.builder()
            .id(1L)
            .creator(creator)
            .title(requestDto.getTitle())
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .location(requestDto.getLocation())
            .visibility(requestDto.getVisibility())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .trip(trip)
            .createdAt(LocalDateTime.now())
            .build();

    given(tripRepository.save(any(Trip.class))).willReturn(trip);
    given(chatRoomRepository.save(any(ChatRoom.class))).willReturn(chatRoom);
    given(tripUserRepository.save(any(TripUser.class))).willReturn(new TripUser());

    // when
    TripResponseDto responseDto = tripService.saveTrip(requestDto, creator);

    // then
    assertThat(responseDto.getTitle()).isEqualTo(trip.getTitle());
    assertThat(responseDto.getLocation()).isEqualTo(trip.getLocation());
    assertThat(responseDto.getStartDate()).isEqualTo(trip.getStartDate());
    assertThat(responseDto.getEndDate()).isEqualTo(trip.getEndDate());
    assertThat(responseDto.getVisibility()).isEqualTo(trip.getVisibility());
  }

  @Test
  @DisplayName("여행 생성 실패 테스트 - 필수 값 누락")
  public void 여행생성_필수값누락테스트() {
    // given
    User creator = User.builder()
            .id(1L)
            .build();

    TripRequestDto requestDto = TripRequestDto.builder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .location("서울")
            .visibility(Visibility.PUBLIC)
            .build();

    // when, then
    assertThrows(NullPointerException.class, () -> {
      tripService.saveTrip(requestDto, creator);
    });
  }

  @Test
  @DisplayName("여행 생성 실패 테스트 - 데이터 접근 예외 발생")
  public void 여행생성_데이터접근예외발생() {
    // given
    User creator = User.builder()
            .id(1L)
            .build();

    TripRequestDto requestDto = TripRequestDto.builder()
            .title("테스트 여행")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .location("서울")
            .visibility(Visibility.PUBLIC)
            .build();

    given(tripRepository.save(any(Trip.class))).willThrow(new DataAccessException("DB 오류 발생") {});

    // when, then
    assertThrows(RuntimeException.class, () -> {
      tripService.saveTrip(requestDto, creator);
    });
  }

  private Trip trip;
  private User creator;
  private final Long VALID_TRIP_ID = 100L;
  private final Long OTHER_TRIP_ID = 999L;
  private final Long USER_ID = 10L;
  private final Long OTHER_USER_ID = 20L;

  @BeforeEach
  void setUp() {
    // Creator와 Trip 객체를 미리 만들어 둠
    creator = User.builder().id(USER_ID).build();
    trip = Trip.builder()
            .id(VALID_TRIP_ID)
            .creator(creator)
            .title("Old Title")
            .build();
  }

  // =============== modifyTrip Tests ================
  @Test
  @DisplayName("modifyTrip - 정상 수정")
  void modifyTrip_success() {
    // given
    TripRequestDto requestDto = TripRequestDto.builder()
            .title("New Title")
            .build();

    given(tripRepository.findById(VALID_TRIP_ID))
            .willReturn(Optional.of(trip));

    // when
    TripResponseDto responseDto = tripService.modifyTrip(VALID_TRIP_ID, requestDto, USER_ID);

    // then
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getTitle()).isEqualTo("New Title"); // trip.update()가 제목을 바꿨다고 가정
    verify(tripRepository).findById(VALID_TRIP_ID); // findById가 호출되었는지 확인
  }

  @Test
  @DisplayName("modifyTrip - Trip이 존재하지 않아 EntityNotFoundException")
  void modifyTrip_notFound() {
    // given
    TripRequestDto requestDto = TripRequestDto.builder().title("New Title").build();

    given(tripRepository.findById(OTHER_TRIP_ID))
            .willReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class, () -> {
      tripService.modifyTrip(OTHER_TRIP_ID, requestDto, USER_ID);
    });
  }

  @Test
  @DisplayName("modifyTrip - 본인 소유 아님 -> SecurityException")
  void modifyTrip_notCreator() {
    // given
    TripRequestDto requestDto = TripRequestDto.builder().title("New Title").build();

    given(tripRepository.findById(VALID_TRIP_ID))
            .willReturn(Optional.of(trip));

    // when & then
    assertThrows(SecurityException.class, () -> {
      tripService.modifyTrip(VALID_TRIP_ID, requestDto, OTHER_USER_ID);
    });
  }

  // =============== getTripById Tests ================
  @Test
  @DisplayName("getTripById - 정상 조회")
  void getTripById_success() {
    // given
    given(tripRepository.findById(VALID_TRIP_ID))
            .willReturn(Optional.of(trip));

    // when
    TripResponseDto responseDto = tripService.getTripById(VALID_TRIP_ID);

    // then
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getTitle()).isEqualTo("Old Title");
    verify(tripRepository).findById(VALID_TRIP_ID);
  }

  @Test
  @DisplayName("getTripById - Trip 존재하지 않을 때 EntityNotFoundException")
  void getTripById_notFound() {
    // given
    given(tripRepository.findById(anyLong()))
            .willReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class, () -> {
      tripService.getTripById(OTHER_TRIP_ID);
    });
  }

  // =============== removeTripById Tests ================
  @Test
  @DisplayName("removeTripById - 정상 삭제")
  void removeTripById_success() {
    // given
    given(tripRepository.findById(VALID_TRIP_ID))
            .willReturn(Optional.of(trip));

    // when
    String result = tripService.removeTripById(VALID_TRIP_ID, USER_ID);

    // then
    assertThat(result).isEqualTo("여행이 정상적으로 삭제되었습니다.");
    verify(tripRepository).delete(trip); // delete 호출 확인
  }

  @Test
  @DisplayName("removeTripById - Trip이 존재하지 않음 -> EntityNotFoundException")
  void removeTripById_notFound() {
    // given
    given(tripRepository.findById(OTHER_TRIP_ID))
            .willReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class, () -> {
      tripService.removeTripById(OTHER_TRIP_ID, USER_ID);
    });
  }

  @Test
  @DisplayName("removeTripById - 본인 소유가 아니면 SecurityException")
  void removeTripById_notCreator() {
    // given
    given(tripRepository.findById(VALID_TRIP_ID))
            .willReturn(Optional.of(trip));

    // when & then
    assertThrows(SecurityException.class, () -> {
      tripService.removeTripById(VALID_TRIP_ID, OTHER_USER_ID);
    });
  }
}