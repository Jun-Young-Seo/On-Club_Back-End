package com.springboot.club_house_api_server.budget.service;

import com.springboot.club_house_api_server.budget.dto.BudgetResponseDto;
import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public ResponseEntity<?> getAllTransactions(long clubId, long accountId){
        List<BudgetResponseDto> responses = new ArrayList<>();
        List<TransactionEntity> transactions = transactionRepository.findByClubId(clubId);

        for(TransactionEntity transaction : transactions){
            BudgetResponseDto dto = BudgetResponseDto.builder()
                    .transactionId(transaction.getTransactionId())
                    .transactionAmount(transaction.getTransactionAmount())
                    .transactionBalance(transaction.getTransactionBalance())
                    .transactionCategory(transaction.getTransactionCategory())
                    .transactionDate(transaction.getTransactionDate())
                    .transactionDescription(transaction.getTransactionDescription())
                    .transactionDetail(transaction.getTransactionDetail())
                    .transactionMemo(transaction.getTransactionMemo())
                    .transactionType(transaction.getTransactionType())
                    .build();
            responses.add(dto);
        }
        return ResponseEntity.ok(responses);
    }
}
