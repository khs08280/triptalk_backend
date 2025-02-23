package com.triptalk.triptalk.dto.requestDto;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.enums.InvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationRequestDto {

  @NotNull(message = "초대할 유저 닉네임은 필수입니다.")
  private String inviteeNickname;

}
