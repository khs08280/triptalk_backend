package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Place;
import com.triptalk.triptalk.domain.enums.PlaceType;
import com.triptalk.triptalk.dto.requestDto.PlaceRequestDto;
import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import com.triptalk.triptalk.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

  @InjectMocks
  private PlaceService placeService;

  @Mock
  private PlaceRepository placeRepository;

  @Test
  @DisplayName("이미 존재하는 Place 반환")
  void getOrCreatePlace_ExistingPlace_ReturnsPlace() {
    // Given
    Long placeId = 1L;
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .placeId(placeId)
            .placeType(PlaceType.NAVER) // 타입은 중요하지 않음
            .build();

    Place existingPlace = Place.builder().name("Existing Place").build();
    ReflectionTestUtils.setField(existingPlace, "id", placeId); // Set id
    when(placeRepository.findById(placeId)).thenReturn(Optional.of(existingPlace));

    // When
    Place result = placeService.getOrCreatePlace(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(placeId);
    assertThat(result.getName()).isEqualTo("Existing Place");
    verify(placeRepository, times(1)).findById(placeId);
    verify(placeRepository, never()).save(any(Place.class)); // save()는 호출되지 않아야 함
  }

  @Test
  @DisplayName("NAVER 타입의 새 Place 생성 성공")
  void getOrCreatePlace_NaverPlace_CreatesAndReturnsPlace() {
    // Given

    PlaceRequestDto placeRequestDto = PlaceRequestDto.builder()
            .category("음식점")
            .address("서울시 강남구")
            .roadAddress("서울시 강남구 도로명주소")
            .mapx(127)
            .mapy(37)
            .build();

    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .name("New Naver Place")
            .placeType(PlaceType.NAVER)
            .place(placeRequestDto)
            .build();

    Place placeToSave = Place.builder()
            .placeType(PlaceType.NAVER)
            .name(requestDto.getName())
            .build();
    Place savedPlace = Place.builder()
            .placeType(PlaceType.NAVER)
            .name(requestDto.getName())
            .build();

    ReflectionTestUtils.setField(savedPlace, "id", 2L);

    when(placeRepository.save(any(Place.class))).thenReturn(savedPlace);

    // When
    Place result = placeService.getOrCreatePlace(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2L); // Assuming savedPlace has id 2
    assertThat(result.getName()).isEqualTo("New Naver Place");
    assertThat(result.getPlaceType()).isEqualTo(PlaceType.NAVER);
    verify(placeRepository, times(1)).save(any(Place.class));
  }

  @Test
  @DisplayName("GOOGLE 타입의 새 Place 생성 성공")
  void getOrCreatePlace_GooglePlace_CreatesAndReturnsPlace() {
    // Given
    PlaceRequestDto placeRequestDto = PlaceRequestDto.builder()
            .googlePlaceId("ChIJ...")
            .category("관광명소")
            .address("서울시 종로구")
            .latitude(37.5)
            .longitude(127.0)
            .rating(4.5)
            .website("http://example.com")
            .phoneNumber("+82-2-123-4567")
            .photoUrls("[\"http://example.com/photo1.jpg\"]") // JSON 형식의 문자열
            .types("[\"point_of_interest\", \"establishment\"]")    // JSON 형식의 문자열
            .openingHours("[\"월-금: 09:00-18:00\"]")           // JSON 형식의 문자열
            .build();
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .name("New Google Place")
            .placeType(PlaceType.GOOGLE)
            .place(placeRequestDto)
            .build();

    Place placeToSave = Place.builder()
            .placeType(PlaceType.GOOGLE)
            .name(requestDto.getName())
            .googlePlaceId(requestDto.getPlace().getGooglePlaceId()) // googlePlaceId 설정
            .build();

    Place savedPlace = Place.builder()
            .placeType(PlaceType.GOOGLE)
            .name(requestDto.getName())
            .googlePlaceId(requestDto.getPlace().getGooglePlaceId())
            .build();
    ReflectionTestUtils.setField(savedPlace, "id", 3L); // Set id

    when(placeRepository.save(any(Place.class))).thenReturn(savedPlace);

    // When
    Place result = placeService.getOrCreatePlace(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3L); // Assuming savedPlace has id 3
    assertThat(result.getName()).isEqualTo("New Google Place");
    assertThat(result.getPlaceType()).isEqualTo(PlaceType.GOOGLE);
    assertThat(result.getGooglePlaceId()).isEqualTo("ChIJ..."); // googlePlaceId 확인
    verify(placeRepository, times(1)).save(any(Place.class));
  }

  @Test
  @DisplayName("잘못된 PlaceType 또는 PlaceId로 예외 발생")
  void getOrCreatePlace_InvalidInput_ThrowsException() {
    // Given
    ScheduleRequestDto requestDto = ScheduleRequestDto.builder()
            .placeType(PlaceType.CUSTOM) // CUSTOM 타입인데 Place를 찾으려고 시도
            .build();

    // When & Then
    assertThatThrownBy(() -> placeService.getOrCreatePlace(requestDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid source type or place ID");
    verify(placeRepository, never()).save(any(Place.class)); // save()는 호출되지 않아야 함
  }
}