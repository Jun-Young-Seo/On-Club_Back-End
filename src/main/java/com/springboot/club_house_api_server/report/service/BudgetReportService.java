package com.springboot.club_house_api_server.report.service;

import com.springboot.club_house_api_server.budget.dto.CategorySummaryDto;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.budget.service.TransactionService;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import com.springboot.club_house_api_server.openai.analyze.service.OpenAIService;
import com.springboot.club_house_api_server.report.dto.BudgetChartDataDto;
import com.springboot.club_house_api_server.report.dto.BudgetReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetReportService {
    private final TransactionService transactionService;
    private final MembershipService membershipService;
    private final TransactionRepository transactionRepository;
    private final ClubRepository clubRepository;
    private final OpenAIService openAIService;
    private final MembershipRepository membershipRepository;

    public ResponseEntity<?> getAIBudgetReport(Long clubId, LocalDate targetMonth) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("clubId에 해당하는 club이 없습니다.");
        }

        LocalDateTime startDate = targetMonth.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth()).atTime(23, 59, 59);
        System.out.println("Input : "+clubId);
        Long income = Optional.ofNullable(transactionRepository.findTotalIncome(clubId, startDate, endDate)).orElse(0L);
        Long expense = Optional.ofNullable(transactionRepository.findTotalExpense(clubId, startDate, endDate)).orElse(0L);
        Long subscriptionFee = Optional.ofNullable(transactionRepository.totalAmountSubscription(clubId, startDate, endDate)).orElse(0L);

        List<CategorySummaryDto> categorizedTransactions = transactionRepository.findTotalAmountGroupedByCategory(clubId, startDate, endDate);

        int memberCount = Math.toIntExact(membershipRepository.countAllMemberships(clubId));
        System.out.println(memberCount);
        int feePerMember = 0;
        if(memberCount != 0) {
            feePerMember = (int) (subscriptionFee / memberCount);
        }
        System.out.println(feePerMember);
        BudgetReportDto reportDto = BudgetReportDto.builder()
                .month(targetMonth.toString())
                .totalIncome(income)
                .totalExpense(expense)
                .netProfit(income - expense)
                .membershipFee(subscriptionFee)
                .memberCount(memberCount)
                .feePerMember(feePerMember)
                .categorySummary(categorizedTransactions)
                .build();


        return openAIService.writeBudgetReportWithAI(reportDto);
    }

    public ResponseEntity<?> getBudgetReportChartData(Long clubId, LocalDate targetMonth) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("clubId에 해당하는 club이 없습니다.");
        }

        LocalDateTime startDate = targetMonth.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth()).atTime(23, 59, 59);
        Long income = Optional.ofNullable(transactionRepository.findTotalIncome(clubId, startDate, endDate)).orElse(0L);
        Long expense = Optional.ofNullable(transactionRepository.findTotalExpense(clubId, startDate, endDate)).orElse(0L);
        List<CategorySummaryDto> categorizedTransactions = transactionRepository.findTotalAmountGroupedByCategory(clubId, startDate, endDate);

        BudgetChartDataDto chartDataDto = BudgetChartDataDto.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .categorySummaries(categorizedTransactions)
                .build();

        return ResponseEntity.ok(chartDataDto);
    }
}
