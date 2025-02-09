package com.triptalk.triptalk.dto.requestDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequestDto {

  @NotNull(message = "scheduleId는 필수값입니다.")
  private Long scheduleId;

  @NotBlank(message = "카테고리는 공백일 수 없습니다.")
  @Size(max = 255, message = "카테고리는 최대 255자 이하여야 합니다.")
  private String category;

  @NotNull(message = "금액(amount)은 필수값입니다.")
  @DecimalMin(value = "0.0", inclusive = true, message = "금액은 0 이상이어야 합니다.")
  @Digits(integer = 10, fraction = 2, message = "금액은 최대 10자리 정수 및 2자리 소수까지 가능합니다.")
  private BigDecimal amount;

  @Size(max = 2000, message = "메모(memo)는 최대 2000자 이하여야 합니다.")
  private String memo;
}