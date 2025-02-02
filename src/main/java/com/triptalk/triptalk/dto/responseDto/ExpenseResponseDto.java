package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.Expense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDto {

  private Long id;
  private Long scheduleId;
  private String category;
  private BigDecimal amount;
  private String memo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 엔티티 -> DTO 변환 메서드
  public static ExpenseResponseDto from(Expense expense) {
    return ExpenseResponseDto.builder()
            .id(expense.getId())
            .scheduleId(expense.getSchedule().getId())
            .category(expense.getCategory())
            .amount(expense.getAmount())
            .memo(expense.getMemo())
            .createdAt(expense.getCreatedAt())
            .updatedAt(expense.getUpdatedAt())
            .build();
  }
}