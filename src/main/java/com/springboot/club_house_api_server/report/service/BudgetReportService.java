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

    public ResponseEntity<?> getAIBudgetReport(Long clubId, int year, int month) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("clubId에 해당하는 club이 없습니다.");
        }

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime startDate = firstDay.atStartOfDay();
        LocalDateTime endDate = lastDay.atTime(23, 59, 59);

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

        BudgetReportDto reportDto = BudgetReportDto.builder()
                .year(year)
                .month(month)
                .totalIncome(income)
                .totalExpense(expense)
                .netProfit(income - expense)
                .membershipFee(subscriptionFee)
                .memberCount(memberCount)
                .feePerMember(feePerMember)
                .categorySummary(categorizedTransactions)
                .build();


        return openAIService.writeBudgetReportWithAI(clubId, reportDto);
    }

    public ResponseEntity<?> getBudgetReportChartData(Long clubId, int year, int month) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("clubId에 해당하는 club이 없습니다.");
        }



        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime startDate = firstDay.atStartOfDay();
        LocalDateTime endDate = lastDay.atTime(23, 59, 59);

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
