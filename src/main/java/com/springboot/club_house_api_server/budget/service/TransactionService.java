package com.springboot.club_house_api_server.budget.service;

import com.springboot.club_house_api_server.budget.dto.*;
import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.account.repository.ClubAccountRepository;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.jwt.customobj.ClubUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ClubRepository clubRepository;
    private final ClubAccountRepository clubAccountRepository;

    //클럽 전체 계좌 거래내역 모아보기용
    public ResponseEntity<?> getAllTransactions(long clubId){
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

    public ResponseEntity<?> getAllTransactionByAccountId(long accountId){
        List<TransactionEntity> transactions = transactionRepository.findByAccountId(accountId);
        List<BudgetResponseDto> responses = new ArrayList<>();

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

    public ResponseEntity<?> updateTransaction(BudgetResponseDto dto){
        long transactionId = dto.getTransactionId();
        int updatedRows = transactionRepository.updateTransaction(
                transactionId,
                dto.getTransactionDate(),
                dto.getTransactionType(),
                dto.getTransactionAmount(),
                dto.getTransactionBalance(),
                dto.getTransactionCategory(),
                dto.getTransactionDescription(),
                dto.getTransactionMemo(),
                dto.getTransactionDetail()
        );

        //한 개의 행도 업데이트 되지 않았다면 id 오류
        if(updatedRows==0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction ID에 해당하는 거래가 없습니다.");
        }
        TransactionEntity t = transactionRepository.getTransactionEntityByTransactionId(transactionId);
        BudgetResponseDto response = BudgetResponseDto.builder()
                .transactionId(t.getTransactionId())
                .transactionDate(t.getTransactionDate())
                .transactionType(t.getTransactionType())
                .transactionAmount(t.getTransactionAmount())
                .transactionBalance(t.getTransactionBalance())
                .transactionCategory(t.getTransactionCategory())
                .transactionDescription(t.getTransactionDescription())
                .transactionMemo(t.getTransactionMemo())
                .transactionDetail(t.getTransactionDetail())
                .build();
        return ResponseEntity.ok(response);
    }


    public ResponseEntity<?> addNewTransaction(AddNewTransactionDto dto){
        Optional<ClubEntity> clubOpt = clubRepository.findById(dto.getClubId());
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        Optional<ClubAccountEntity> accountOpt = clubAccountRepository.findById(dto.getClubAccountId());
        if(accountOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("accountId 해당하는 계좌가 없습니다.");
        }
        TransactionEntity newTransaction = new TransactionEntity(
                accountOpt.get(),
                clubOpt.get(),
                dto.getTransactionDate(),
                dto.getTransactionType(),
                dto.getTransactionAmount(),
                dto.getTransactionBalance(),
                dto.getTransactionCategory(),
                dto.getTransactionDescription(),
                dto.getTransactionMemo(),
                dto.getTransactionDetail()
        );
        transactionRepository.save(newTransaction);

        BudgetResponseDto response = BudgetResponseDto.builder()
                .transactionId(newTransaction.getTransactionId())
                .transactionAmount(newTransaction.getTransactionAmount())
                .transactionBalance(newTransaction.getTransactionBalance())
                .transactionCategory(newTransaction.getTransactionCategory())
                .transactionDate(newTransaction.getTransactionDate())
                .transactionDescription(newTransaction.getTransactionDescription())
                .transactionDetail(newTransaction.getTransactionDetail())
                .transactionMemo(newTransaction.getTransactionMemo())
                .transactionType(newTransaction.getTransactionType())
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getBudgetInfo(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        //null이면 0
        Long mainAccountId = clubOpt.get().getClubMainAccountId();
        if(mainAccountId==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("메인 계좌가 설정되지 않았습니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        long income = transactionRepository.getMonthlyIncomeByAccountId(mainAccountId, startOfMonth, now);
        long expense = transactionRepository.getMonthlyExpenseByAccountId(mainAccountId, startOfMonth, now);
        Optional<TransactionEntity> balanceOpt = transactionRepository.findTopByAccount_AccountIdOrderByTransactionDateDesc(mainAccountId);
        if(balanceOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("balnce err");
        }
        int balance = balanceOpt.get().getTransactionBalance();
//        Long balance = transactionRepository.getLatestMonthlyBalanceByAccountId(mainAccountId, startOfMonth, now);
        long monthlySurplus = income - expense;

        DashBoardMonthInfoDto response = DashBoardMonthInfoDto.builder()
                .monthlyIncome(income)
                .monthlyExpense(expense)
                .balance(balance)
                .monthlySurplus(monthlySurplus)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getLastThreeTransactions(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        Long mainAccountId = clubOpt.get().getClubMainAccountId();
        if(mainAccountId==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("메인 계좌가 설정되지 않았습니다.");
        }
        List<TransactionEntity> lastThreeTransactions = transactionRepository.getLastThreeTransactions(mainAccountId);
        List<BudgetResponseDto> response = new ArrayList<>();
        for(TransactionEntity transaction : lastThreeTransactions){
            BudgetResponseDto b = BudgetResponseDto.builder()
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
            response.add(b);
        }
        return ResponseEntity.ok(response);
    }

    //For chart.js
    public ResponseEntity<?> findIncomeSummary(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();
        Long mainAccountId = club.getClubMainAccountId();
        if(mainAccountId==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 메인게좌가 설정되지 않았습니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<IncomeSummaryDto> response = transactionRepository.findIncomeSummary(clubId,startOfMonth,now);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> findExpenseSummary(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();
        Long mainAccountId = club.getClubMainAccountId();
        if(mainAccountId==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 메인계좌가 설정되지 않았습니다.");
        }
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<ExpenseSummaryDto> response = transactionRepository.findExpenseSummary(clubId,startOfMonth,now);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> findMonthlyDataSummary(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();
        Long mainAccountId = club.getClubMainAccountId();
        if(mainAccountId==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 메인계좌가 설정되지 않았습니다.");
        }
        List<MonthlyDataDto> response = new ArrayList<>();

        YearMonth now = YearMonth.now();
        YearMonth oneYearAgo = now.minusMonths(12);
        for (YearMonth month = oneYearAgo; month.isBefore(now); month = month.plusMonths(1)) {
            LocalDateTime startDate = month.atDay(1).atStartOfDay();
            LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59);

            long income = transactionRepository.getMonthlyIncomeByAccountId(mainAccountId, startDate, endDate);
            long expense = transactionRepository.getMonthlyExpenseByAccountId(mainAccountId, startDate, endDate);

            response.add(new MonthlyDataDto(month, income, expense));
        }
        return ResponseEntity.ok(response);
    }

}
