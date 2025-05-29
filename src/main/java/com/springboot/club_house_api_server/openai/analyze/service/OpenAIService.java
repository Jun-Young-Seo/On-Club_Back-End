package com.springboot.club_house_api_server.openai.analyze.service;

import com.springboot.club_house_api_server.budget.dto.CategorySummaryDto;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.game.dto.GamePlayStatDto;
import com.springboot.club_house_api_server.game.dto.ScoreStatDto;
import com.springboot.club_house_api_server.openai.analyze.dto.ClubDescriptionDto;
import com.springboot.club_house_api_server.openai.analyze.dto.CustomRequestDto;
import com.springboot.club_house_api_server.openai.analyze.dto.MessageDto;
import com.springboot.club_house_api_server.openai.analyze.dto.ResponseDto;
import com.springboot.club_house_api_server.report.dto.BudgetReportDto;
import com.springboot.club_house_api_server.report.dto.GameStatDto;
import com.springboot.club_house_api_server.report.dto.MemberChartDataDto;
import com.springboot.club_house_api_server.report.entity.ReportEntity;
import com.springboot.club_house_api_server.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

@Service
public class OpenAIService {
    private final RestTemplate restTemplate;
    private final String openAIURL;
    private final String gptModel;
    private final ReportRepository reportRepository;
    private final ClubRepository clubRepository;

    public OpenAIService(@Value("${openai.api.url}")String openAIURL,
                         @Value("${openai.model}")String gptModel,
                         RestTemplate restTemplate, ReportRepository reportRepository, ClubRepository clubRepository) {
        this.openAIURL = openAIURL;
        this.gptModel = gptModel;
        this.restTemplate = restTemplate;
        this.reportRepository = reportRepository;
        this.clubRepository = clubRepository;
    }

    public String getGptResponse(String prompt) {
        CustomRequestDto requestDto = new CustomRequestDto(
                gptModel,
                List.of(new MessageDto("user", prompt))
        );

        ResponseDto response = restTemplate.postForObject(openAIURL, requestDto, ResponseDto.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }

        return "OpenAI 응답이 없습니다.";
    }

    public ResponseEntity<?> writeClubDescriptionWithAI(ClubDescriptionDto dto) {
        String clubName = dto.getClubName();
        String region = dto.getRegion();
        String careerRange = dto.getCareerRange();
        String purpose = dto.getPurpose();

        String prompt = buildClubDescriptionPrompt(clubName, region, careerRange, purpose);
        String result = getGptResponse(prompt);

        return ResponseEntity.ok(result);
    }

    @Transactional
    public ResponseEntity<?> writeBudgetReportWithAI(Long clubId, BudgetReportDto dto) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 club이 없습니다.");
        }
        ClubEntity club = clubOpt.get();

        Optional<ReportEntity> existingOpt = reportRepository.findByClubAndYearAndMonth(club, dto.getYear(), dto.getMonth());

        String prompt = buildBudgetAnalysisPrompt(dto);
        String result = getGptResponse(prompt);

        //이미 엔티티가 존재하는 경우
        //덮어 씌워지기 때문에 Set
        if(existingOpt.isPresent()){
            ReportEntity reportEntity = existingOpt.get();
            reportEntity.setAiBudgetReport(result);
            return ResponseEntity.ok(result);
        }
        //엔티티가 없었다면 새로 생성
        ReportEntity reportEntity = ReportEntity.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .club(club)
                .aiBudgetReport(result)
                .build();

        reportRepository.save(reportEntity);

        return ResponseEntity.ok(result);

    }

    @Transactional
    public ResponseEntity<?> writeMemberReportWithAI(Long clubId, MemberChartDataDto dto){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("clubId에 해당하는 club이 없습니다.");
        }
        ClubEntity club = clubOpt.get();

        String prompt = buildMemberAnalysisPrompt(dto);
        String result = getGptResponse(prompt);

        Optional<ReportEntity> isAlreadyExistOpt = reportRepository.findByClubAndYearAndMonth(club, dto.getYear(), dto.getMonth());
        if(isAlreadyExistOpt.isPresent()){
            ReportEntity reportEntity = isAlreadyExistOpt.get();
            reportEntity.setAiMemberReport(result);
            return ResponseEntity.ok(result);
        }
        ReportEntity reportEntity = ReportEntity.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .club(club)
                .aiMemberReport(result)
                .build();

        reportRepository.save(reportEntity);

        return ResponseEntity.ok(result);
    }

    private String buildClubDescriptionPrompt(String clubName, String region, String careerRange, String purpose) {
        return String.format("""
        아래 정보를 바탕으로 테니스 클럽의 소개 문장을 작성해줘.

        ✅ 목적:
        새로운 회원이 이 소개글을 보고 클럽에 가입하고 싶도록 만드는 것
        이 문장은 clubDescription 필드에 포함될건데, "클럽 한 줄 소개" 의 역할을 할 거야.
        자세한 소개 페이지는 따로 있으니까, 너무 자세하게 작성하지 마.
        
        ✅ 조건:
        - 문장은 1~2문장 정도로 자연스럽게 작성
        - 너무 딱딱하거나 공식적이지 않게, 따뜻하고 자신감 있는 어조로 작성
        - 적절하게 예쁜 이모지를 섞어줘
        - 소개 외의 설명, 안내 문구는 절대 포함하지 마

        ✨ 예시
        클럽 이름: 그랜드슬램
        활동 지역: 서울 송파구
        구력 분포: 1~3년
        목표: 친목

        👉 출력:
        서울 송파구에서 활동하는 그랜드슬램 🎾은 1~3년차 테린이들이 모여 즐겁게 운동하고 우정을 나누는 따뜻한 클럽이에요. 테니스 실력보다 사람을 소중히 생각하는 우리와 함께해요! 😊

        ✨ 입력 정보
        클럽 이름: %s
        활동 지역: %s
        구력 분포: %s
        목표: %s

        👉 출력:
        """, clubName, region, careerRange, purpose);
    }

    private String buildBudgetAnalysisPrompt(BudgetReportDto dto) {
        StringBuilder categorySection = new StringBuilder();
        for (CategorySummaryDto summary : dto.getCategorySummary()) {
            categorySection.append(String.format(
                    "- %s: %,d원%n",
                    summary.getCategory(),
                    summary.getAmount()));
        }

        return String.format("""
        아래는 테니스 클럽의 %s 예산 데이터야.
        지출은 수 앞에 -가 포함되어 있어.
        
        ✅ 특징:
        이 데이터는 테니스 클럽의 예산 관리 데이터야.
        테니스 클럽의 예산 관리는 흑자를 보고 이득을 보기 위함이 아니라, 안정적인 클럽 운영에 그 목표가 있어.
        다만 흑자가 많이 나서 쌓여 있는 돈이 큰 경우에는 다양한 투자처를 모색할 수 있지. 단 안정적인 투자여야 해.(예 : 채권, 적금 등)
        
        ✅ 목적:
        테니스 클럽 운영진에게 예산 관리에 대한 다양한 인사이트를 제공하고, 다음 달 운영 방향에 참고가 되도록 하기 위함.

        ✅ 분석 요청:
        - 만약 클럽의 예산이 적자인 경우, 회비를 어떻게 조정해야 할지에 대해 분석해줘.
        - 클럽의 예산이 흑자인 경우에는 회비가 과할 수 있어. 줄이는 방법에 대해 고민해줘.
        - 클럽의 회비를 조정해야 한다고 판단한 경우, 그 금액이 어떻게 나왔는지에 대해서도 이야기해주면 좋아.
        - 클럽의 주 지출은 테니스 코트 대여비, 용품비 등이야. 이 항목들이 너무 크다고 생각하면 지적해줘.
        - 마지막엔 다음 달 운영을 위한 제안을 한두 줄 포함해줘.(예 : 회비가 너무 높아 예산이 이월됩니다. 회비를 x원 줄이는 것을 추천드립니다.)
        
        ✅ 구조:
        - 보고서는 아래와 같이 **구조화된 영역**으로 구성되면 좋아:
        - `## 요약` → 이번 달 예산 상태 요약 (흑자/적자 등)
        - `## 수입/지출 분석` → 항목별 분석, 주된 특징
        - `## 회비 조정 제안` → 필요 시 회비 조정과 그 근거
        - `## 다음 달 운영 방향` → 추천 액션/운영 방향 제안
        - 꼭 위의 구조를 따를 필요는 없고, 너가 직접 판단해서 글에 적절한 구조를 선택해서 제목을 달아 구조를 만들어줘.
        - 또, 반환되는 글의 형태는 "마크다운 문법"에 맞춰줘.
        - 각 구조의 제목 앞에는 적절하게 이모지를 포함해줘. 어울리는 걸로.
        - 각 구조의 내용에는 "-"를 추가해서 리스트처럼 표현해줘.
        - 그리고 요청한 내용에 대해서만 답변해줘. "더 필요한게 있으면 말해줘."와 같은 필요 없는 문장은 포함하지 마.
        
        ✅ 조건:
        - 문장은 4~6문장 정도로 자연스럽고 자신감 있게 작성
        - 너무 딱딱하거나 숫자 나열식이 아니라, 통찰력 있게 작성해줘

        ✨ 입력 데이터
        - 총 수입: %,d원
        - 총 지출: %,d원
        - 회비 수입: %,d원
        - 회원 수: %d명
        - 1인당 회비: %,d원

        📊 항목별 수입/지출 요약:
        %s

        👉 출력:
        """,
                dto.getMonth(),
                dto.getTotalIncome(),
                dto.getTotalExpense(),
                dto.getMembershipFee(),
                dto.getMemberCount(),
                dto.getFeePerMember(),
                categorySection.toString()
        );
    }

    private String buildMemberAnalysisPrompt(MemberChartDataDto dto) {
        String topAttendantName = dto.getMostAttendantMember().isEmpty() ? "없음" : dto.getMostAttendantMember().get(0).getUserName();
        String topGamePlayerName = dto.getMostManyGamesMember().isEmpty() ? "없음" : dto.getMostManyGamesMember().get(0).getUserName();
        String topScorerName = dto.getMostWinnerMember().isEmpty() ? "없음" : dto.getMostWinnerMember().get(0).getUserName();


        return String.format("""
    아래는 테니스 클럽의 회원 통계 데이터야.
    
    ✅ 특징:
    이 데이터는 테니스 클럽의 **회원 활동 및 구성 분석**을 통해, 운영진이 다음 달 회원 관리 전략을 수립하는 데 도움을 주기 위한 자료야.
    게스트는 클럽에 소속된 회원은 아니지만, 따로 찾아서 참가를 신청했다는 점에서 그 의의가 있어.
    게스트들을 클럽의 회원이 되도록 유도하는 것이 좋은 전략이 될거야.
    또, 회원이 많다고 해서 무조건 좋은 클럽은 아니야.
    좋은 클럽이란 "활동하는 회원이 많은 클럽"이야. 이 말은 소속된 회원 중 대부분이 활동에 참여한다는 것을 의미해.
    
    ✅ 분석 요청:
    - 현재 정회원 수와 지난 한 달간 신규 가입자 수를 비교해 회원 성장 추세를 파악해줘.
    - 남성/여성 회원 비율을 분석해서 성비 균형에 대한 인사이트를 제공해줘. 성비가 비슷하게 맞아야 다른 사람들이 봤을 때 참여하기 좋은 클럽일 수 있어.
    - 하지만 남성 또는 여성으로만 구성되어 있다면 남성 또는 여성 전용 클럽일 수 있으니 언급하지 않아도 돼.
    - 게스트 수(누적 및 최근 1개월)를 분석하고, 이를 통해 **외부 유입 전략에 대한 의견**을 줘. (예 : SNS 홍보 강화, 친구를 게스트로 데려오면 상품 증정 등)
    - 이벤트와 게임에서 활동량이 많은 회원들에 대해서도 언급해줘. 이 회원들에 대한 적절한 포상이 회원 활동 유도에 도움이 될 수 있어. 언급은 회원의 이름만을 언급하자.(userName)
    - 마지막엔 운영에 대한 제안 한두 줄을 넣어줘. 이 제안을 보고 운영진이 운영에 대한 인사이트를 얻을 수 있어야 해.
    
    ✅ 구조:
    - 보고서는 아래의 **마크다운 기반** 구조로 구성되면 좋아:
    - `## 📊 회원 통계 요약` → 회원 수, 증가 추세 등
    - `## 👥 성비 및 구성 분석` → 남녀 비율 및 인사이트
    - `## 🚪 게스트 유입 분석` → 게스트 방문 및 유입 제안
    - `## 🏅 핵심 활동 회원` → 활동 많은 회원 소개
    - `## 📌 다음 달 운영 방향` → 추천 전략 및 행동 제안
    
    - 꼭 위 구조를 따를 필요는 없고, 직접 상황에 맞춰 스스로 더 적절하게 나눠도 좋아.
    - 글은 반드시 마크다운 문법을 따라야 하고, **적절한 이모지와 제목**, 그리고 `-` 리스트 형태로 표현해줘.
    - 마지막에 불필요한 인사말이나 마무리 멘트는 절대 포함하면 안돼.
    
    ✨ 입력 데이터
    - 총 정회원 수: %d명
    - 1개월간 신규 가입자 수: %d명
    - 누적 게스트 수: %d명
    - 1개월간 게스트 수: %d명
    - 1개월간 이벤트 수: %d개
    - 전체 이벤트 총 참석 횟수: %d회
    - 남성 회원: %d명
    - 여성 회원: %d명
    - 이벤트 최다 참석자: %s
    - 게임 최다 참가자: %s
    - 총 득점 1위: %s
    
    👉 출력:
    """,
                dto.getHowManyMembers(),
                dto.getHowManyMembersBetweenOneMonth(),
                dto.getHowManyAccumulatedGuests(),
                dto.getHowManyGuestsBetweenOneMonth(),
                dto.getHowManyEventsBetweenOneMonth(),
                dto.getAttendanceCount(),
                dto.getMaleMembers(),
                dto.getFemaleMembers(),
                topAttendantName,
                topGamePlayerName,
                topScorerName
        );
    }

}


