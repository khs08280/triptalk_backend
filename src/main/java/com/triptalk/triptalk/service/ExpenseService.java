package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Expense;
import com.triptalk.triptalk.domain.entity.Schedule;
import com.triptalk.triptalk.dto.requestDto.ExpenseRequestDto;
import com.triptalk.triptalk.dto.responseDto.ExpenseResponseDto;
import com.triptalk.triptalk.repository.ExpenseRepository;
import com.triptalk.triptalk.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ScheduleRepository scheduleRepository; // scheduleId로 Schedule 조회

  /**
   * 비용 생성
   */
  public ExpenseResponseDto createExpense(ExpenseRequestDto requestDto) {
    // 1) scheduleId로 Schedule 엔티티 조회
    Schedule schedule = scheduleRepository.findById(requestDto.getScheduleId())
            .orElseThrow(() -> new IllegalArgumentException("해당 scheduleId가 존재하지 않습니다. id=" + requestDto.getScheduleId()));

    // 2) Expense 엔티티 생성
    Expense expense = new Expense(
            null,
            schedule,
            requestDto.getCategory(),
            requestDto.getAmount(),
            requestDto.getMemo(),
            null, // createdAt (Auditing)
            null  // updatedAt (Auditing)
    );

    // 3) DB 저장
    Expense savedExpense = expenseRepository.save(expense);

    // 4) ResponseDto로 변환하여 반환
    return ExpenseResponseDto.from(savedExpense);
  }

  /**
   * 비용 단건 조회
   */
  @Transactional(readOnly = true)
  public ExpenseResponseDto getExpense(Long expenseId) {
    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new IllegalArgumentException("해당 Expense가 없습니다. id=" + expenseId));
    return ExpenseResponseDto.from(expense);
  }

  /**
   * 전체 비용 조회
   */
  @Transactional(readOnly = true)
  public List<ExpenseResponseDto> getAllExpenses() {
    List<Expense> expenses = expenseRepository.findAll();
    return expenses.stream()
            .map(ExpenseResponseDto::from)
            .toList();
  }

  /**
   * 비용 수정
   */
  public ExpenseResponseDto updateExpense(Long expenseId, ExpenseRequestDto requestDto) {
    // 1) 기존 데이터 조회
    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new IllegalArgumentException("해당 Expense가 없습니다. id=" + expenseId));

    // 2) scheduleId 변경이 필요하다면 (optional)
    if (!expense.getSchedule().getId().equals(requestDto.getScheduleId())) {
      Schedule newSchedule = scheduleRepository.findById(requestDto.getScheduleId())
              .orElseThrow(() -> new IllegalArgumentException("변경할 scheduleId가 존재하지 않습니다. id=" + requestDto.getScheduleId()));
      expense = new Expense(
              expense.getId(),
              newSchedule,
              requestDto.getCategory(),
              requestDto.getAmount(),
              requestDto.getMemo(),
              expense.getCreatedAt(),
              expense.getUpdatedAt()
      );
    } else {
      // 3) scheduleId가 동일할 경우, 다른 필드만 갱신
      expense = new Expense(
              expense.getId(),
              expense.getSchedule(),
              requestDto.getCategory(),
              requestDto.getAmount(),
              requestDto.getMemo(),
              expense.getCreatedAt(),
              expense.getUpdatedAt()
      );
    }

    // 4) DB에 저장(병합)
    Expense updatedExpense = expenseRepository.save(expense);

    // 5) DTO 변환
    return ExpenseResponseDto.from(updatedExpense);
  }

  /**
   * 비용 삭제
   */
  public void deleteExpense(Long expenseId) {
    if (!expenseRepository.existsById(expenseId)) {
      throw new IllegalArgumentException("삭제할 Expense가 없습니다. id=" + expenseId);
    }
    expenseRepository.deleteById(expenseId);
  }
}
