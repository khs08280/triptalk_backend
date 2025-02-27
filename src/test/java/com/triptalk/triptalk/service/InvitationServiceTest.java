package com.triptalk.triptalk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.triptalk.triptalk.domain.entity.*;
import com.triptalk.triptalk.domain.enums.InvitationStatus;
import com.triptalk.triptalk.dto.responseDto.InvitationResponseDto;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

  @Mock
  private InvitationRepository invitationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private TripUserRepository tripUserRepository;

  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;

  @InjectMocks
  private InvitationService invitationService;

  private User inviter;
  private User invited;
  private Trip trip;
  private Invitation invitation;

  @BeforeEach
  void setUp() {
    // Set up sample data
    inviter = User.builder()
            .id(1L)
            .nickname("InviterNickname")
            .build();

    invited = User.builder()
            .id(2L)
            .nickname("InvitedNickname")
            .build();

    trip = Trip.builder()
            .id(100L)
            .chatRoom(null) // or set a mock ChatRoom if needed
            .build();

    invitation = Invitation.builder()
            .id(10L)
            .trip(trip)
            .inviter(inviter)
            .invited(invited)
            .status(InvitationStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  void testGetUserInvitations() {
    // given
    given(invitationRepository.findAllWithDetailsByInvitedIdAndStatus(2L, InvitationStatus.PENDING))
            .willReturn(List.of(invitation));

    // when
    List<InvitationResponseDto> result = invitationService.getUserInvitations(2L);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getInvitedId()).isEqualTo(invited.getId());

    verify(invitationRepository, times(1))
            .findAllWithDetailsByInvitedIdAndStatus(2L, InvitationStatus.PENDING);
  }

  @Test
  void testSendInvitation_Success() {
    // given
    Long tripId = 100L;
    Long inviterId = 1L;
    String invitedNickname = "InvitedNickname";

    given(tripRepository.findById(tripId)).willReturn(Optional.of(trip));
    given(userRepository.findById(inviterId)).willReturn(Optional.of(inviter));
    given(userRepository.findByNickname(invitedNickname)).willReturn(Optional.of(invited));
    given(tripUserRepository.existsByTripIdAndUserId(tripId, invited.getId())).willReturn(false);

    // when
    invitationService.sendInvitation(tripId, inviterId, invitedNickname);

    // then
    verify(invitationRepository, times(1)).save(any(Invitation.class));
  }

  @Test
  void testSendInvitation_AlreadyInvited() {
    // given
    Long tripId = 100L;
    Long inviterId = 1L;
    String invitedNickname = "InvitedNickname";

    given(tripRepository.findById(tripId)).willReturn(Optional.of(trip));
    given(userRepository.findById(inviterId)).willReturn(Optional.of(inviter));
    given(userRepository.findByNickname(invitedNickname)).willReturn(Optional.of(invited));
    given(tripUserRepository.existsByTripIdAndUserId(tripId, invited.getId())).willReturn(true);

    // when & then
    assertThatThrownBy(() ->
            invitationService.sendInvitation(tripId, inviterId, invitedNickname))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 초대된 사용자입니다.");

  }

  @Test
  void testSendInvitation_TripNotFound() {
    // given
    Long tripId = 999L;
    Long inviterId = 1L;
    String invitedNickname = "InvitedNickname";

    given(tripRepository.findById(tripId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
            invitationService.sendInvitation(tripId, inviterId, invitedNickname))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("해당 여행을 찾을 수 없습니다.");
  }

  @Test
  void testCancelInvitation_Success() {
    // given
    Long invitationId = 10L;
    Long userId = 1L;

    given(invitationRepository.findById(invitationId)).willReturn(Optional.of(invitation));

    // when
    invitationService.cancelInvitation(invitationId, userId);

    // then
    assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.CANCELED);
    verify(invitationRepository, times(1)).save(invitation);
  }

  @Test
  void testCancelInvitation_AccessDenied() {
    // given
    Long invitationId = 10L;
    Long userId = 99L; // Not the inviter

    given(invitationRepository.findById(invitationId)).willReturn(Optional.of(invitation));

    // when & then
    assertThatThrownBy(() -> invitationService.cancelInvitation(invitationId, userId))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("초대만 취소할 수 있습니다.");
  }

  @Test
  void testAcceptInvitation_Success() {
    // given
    Long invitationId = 10L;
    User user = invited; // The one who was invited

    given(invitationRepository.findById(invitationId)).willReturn(Optional.of(invitation));

    // when
    invitationService.acceptInvitation(invitationId, user);

    // then
    assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    verify(invitationRepository, times(1)).save(invitation);
    verify(tripUserRepository, times(1)).save(any(TripUser.class));
    verify(chatRoomUserRepository, times(1)).save(any(ChatRoomUser.class));
  }

  @Test
  void testAcceptInvitation_NotTheInvitedUser() {
    // given
    Long invitationId = 10L;
    User anotherUser = User.builder().id(999L).build(); // not the invited

    given(invitationRepository.findById(invitationId)).willReturn(Optional.of(invitation));

    // when & then
    assertThatThrownBy(() -> invitationService.acceptInvitation(invitationId, anotherUser))
            .isInstanceOf(org.springframework.security.access.AccessDeniedException.class)
            .hasMessage("초대받은 사용자만 수락할 수 있습니다.");
  }

  @Test
  void testRejectInvitation_Success() {
    // given
    Long invitationId = 10L;
    Long userId = invited.getId(); // The one who was invited

    given(invitationRepository.findById(invitationId)).willReturn(Optional.of(invitation));

    // when
    invitationService.rejectInvitation(invitationId, userId);

    // then
    assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.REJECTED);
    verify(invitationRepository, times(1)).save(invitation);
  }

  @Test
  void testRejectInvitation_NotTheInvitedUser() {
    // given
    Long invitationId = 10L;
    Long userId = 99L;

    given(invitationRepository.findById(invitationId)).willReturn(Optional.of(invitation));

    // when & then
    assertThatThrownBy(() -> invitationService.rejectInvitation(invitationId, userId))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("초대받은 사용자만 거절할 수 있습니다."); // 원하는 대로 메시지를 검증
  }

  @Test
  void testRejectInvitation_InvitationNotFound() {
    // given
    Long invitationId = 999L;
    Long userId = invited.getId();

    given(invitationRepository.findById(invitationId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> invitationService.rejectInvitation(invitationId, userId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("초대 정보를 찾을 수 없습니다."); // 원하는 대로 메시지를 검증
  }
}