package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.InvitationRequestDto;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.InvitationResponseDto;
import com.triptalk.triptalk.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

  private final InvitationService invitationService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<InvitationResponseDto>>> getUserInvitations(
          @AuthenticationPrincipal User user) {

    List<InvitationResponseDto> invitations = invitationService.getUserInvitations(user.getId());

    return ResponseEntity.ok(ApiResponse.success("초대 목록 조회 성공", invitations));
  }


  @PostMapping("/send/{tripId}")
  public ResponseEntity<ApiResponse<Void>> sendInvitation(
          @PathVariable Long tripId,
          @RequestBody InvitationRequestDto invitationRequestDto,
          @AuthenticationPrincipal User user) {

    invitationService.sendInvitation(tripId, user.getId(), invitationRequestDto.getInviteeNickname());

    return ResponseEntity.ok(ApiResponse.success("초대가 성공적으로 전송되었습니다.", null));
  }


  @PatchMapping("/{invitationId}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancelInvitation(
          @PathVariable Long invitationId,
          @AuthenticationPrincipal User user) {

    invitationService.cancelInvitation(invitationId, user.getId());

    return ResponseEntity.ok(ApiResponse.success("초대가 취소되었습니다.", null));
  }


  @PatchMapping("/{invitationId}/accept")
  public ResponseEntity<ApiResponse<Void>> acceptInvitation(
          @PathVariable Long invitationId,
          @AuthenticationPrincipal User user) {

    invitationService.acceptInvitation(invitationId, user);

    return ResponseEntity.ok(ApiResponse.success("초대를 수락했습니다.", null));
  }


  @PatchMapping("/{invitationId}/reject")
  public ResponseEntity<ApiResponse<Void>> rejectInvitation(
          @PathVariable Long invitationId,
          @AuthenticationPrincipal User user) {

    invitationService.rejectInvitation(invitationId, user.getId());

    return ResponseEntity.ok(ApiResponse.success("초대를 거절했습니다.", null));
  }
}

