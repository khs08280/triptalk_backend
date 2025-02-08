package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Expense;
import com.triptalk.triptalk.domain.entity.Schedule;
import com.triptalk.triptalk.dto.requestDto.ExpenseRequestDto;
import com.triptalk.triptalk.dto.responseDto.ExpenseResponseDto;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
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
  private final ScheduleRepository scheduleRepository;

  public ExpenseResponseDto createExpense(ExpenseRequestDto requestDto) {
    Schedule schedule = scheduleRepository.findById(requestDto.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("해당 일정을 찾을 수 없습니다. Id=" + requestDto.getScheduleId()));


    Expense expense = Expense.builder()
            .schedule(schedule)
            .category(requestDto.getCategory())
            .amount(requestDto.getAmount())
            .memo(requestDto.getMemo())
            .build();

    Expense savedExpense = expenseRepository.save(expense);

    return ExpenseResponseDto.fromEntity(savedExpense);
  }

  @Transactional(readOnly = true)
  public ExpenseResponseDto getExpense(Long expenseId) {
    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 비용 데이터를 찾을 수 없습니다. Id=" + expenseId));
    return ExpenseResponseDto.fromEntity(expense);
  }

  @Transactional(readOnly = true)
  public List<ExpenseResponseDto> getAllExpenses() {
    List<Expense> expenses = expenseRepository.findAll();
    return expenses.stream()
            .map(ExpenseResponseDto::fromEntity)
            .toList();
  }

  public ExpenseResponseDto updateExpense(Long expenseId, ExpenseRequestDto requestDto) {
    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 비용 데이터를 찾을 수 없습니다. Id=" + expenseId));

    expense = expense.updateDetails(requestDto);

    return ExpenseResponseDto.fromEntity(expense);
  }
  public void deleteExpense(Long expenseId) {
    if (!expenseRepository.existsById(expenseId)) {
      throw new ResourceNotFoundException("해당 비용 데이터를 찾을 수 없습니다. id=" + expenseId);
    }
    expenseRepository.deleteById(expenseId);
  }
}
