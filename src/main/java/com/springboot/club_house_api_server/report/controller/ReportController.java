package com.springboot.club_house_api_server.report.controller;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.report.entity.ReportEntity;
import com.springboot.club_house_api_server.report.repository.ReportRepository;
import com.springboot.club_house_api_server.report.service.BudgetReportService;
import com.springboot.club_house_api_server.report.service.MemberReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    private final BudgetReportService budgetReportService;
    private final MemberReportService memberReportService;
    private final ReportRepository reportRepository;
    private final ClubRepository clubRepository;

    @GetMapping("/budget/analyze")
    public ResponseEntity<?> aiBudgetAnalyze(@RequestParam Long clubId, @RequestParam int year, @RequestParam int month){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();

        //보고서는 지난 월 기준 생성이므로 달 하나 줄이기
        if (month == 1) {
            year -= 1;
            month = 12;
        } else {
            month -= 1;
        }
        Optional<ReportEntity> reportOpt = reportRepository.findByClubAndYearAndMonth(club, year, month);
        if(reportOpt.isPresent() && reportOpt.get().getAiBudgetReport() != null){
            return ResponseEntity.ok(reportOpt.get().getAiBudgetReport());
        }

        return budgetReportService.getAIBudgetReport(clubId,year, month);
    }

    @GetMapping("/budget/data")
    public ResponseEntity<?> getChartData(@RequestParam Long clubId, @RequestParam int year, @RequestParam int month){
        //보고서는 지난 월 기준 생성이므로 달 하나 줄이기
        if (month == 1) {
            year -= 1;
            month = 12;
        } else {
            month -= 1;
        }
        return budgetReportService.getBudgetReportChartData(clubId, year, month);
    }

    @GetMapping("/member/data")
    public ResponseEntity<?> getMemberData(@RequestParam Long clubId, @RequestParam int year, @RequestParam int month){
        if (month == 1) {
            year -= 1;
            month = 12;
        } else {
            month -= 1;
        }
        return memberReportService.getMemberReportChartData(clubId, year, month);
    }

    @GetMapping("/member/analyze")
    public ResponseEntity<?> aiMemberAnalyze(@RequestParam Long clubId, @RequestParam int year, @RequestParam int month){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();

        //보고서는 지난 월 기준 생성이므로 달 하나 줄이기
        if (month == 1) {
            year -= 1;
            month = 12;
        } else {
            month -= 1;
        }
        Optional<ReportEntity> reportOpt = reportRepository.findByClubAndYearAndMonth(club, year, month);
        if(reportOpt.isPresent() && reportOpt.get().getAiMemberReport()!=null){
            return ResponseEntity.ok(reportOpt.get().getAiMemberReport());
        }
        return memberReportService.getAIMemberReport(clubId, year, month);
    }
}
