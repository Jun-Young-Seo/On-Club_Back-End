package com.springboot.club_house_api_server.openai.analyze.service;

import com.springboot.club_house_api_server.budget.dto.CategorySummaryDto;
import com.springboot.club_house_api_server.openai.analyze.dto.ClubDescriptionDto;
import com.springboot.club_house_api_server.openai.analyze.dto.CustomRequestDto;
import com.springboot.club_house_api_server.openai.analyze.dto.MessageDto;
import com.springboot.club_house_api_server.openai.analyze.dto.ResponseDto;
import com.springboot.club_house_api_server.report.dto.BudgetReportDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenAIService {
    private final RestTemplate restTemplate;
    private final String openAIURL;
    private final String gptModel;

    public OpenAIService(@Value("${openai.api.url}")String openAIURL,
                         @Value("${openai.model}")String gptModel,
                         RestTemplate restTemplate) {
        this.openAIURL = openAIURL;
        this.gptModel = gptModel;
        this.restTemplate = restTemplate;
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


    public ResponseEntity<?> writeBudgetReportWithAI(BudgetReportDto dto) {
        String prompt = buildBudgetAnalysisPrompt(dto);
        String result = getGptResponse(prompt);

        return ResponseEntity.ok(result);

    }
    private String buildClubDescriptionPrompt(String clubName, String region, String careerRange, String purpose) {
        return String.format("""
        아래 정보를 바탕으로 테니스 클럽의 소개 문장을 작성해줘.

        ✅ 목적:
        새로운 회원이 이 소개글을 보고 클럽에 가입하고 싶도록 만드는 것

        ✅ 조건:
        - 문장은 2~3문장 정도로 자연스럽게 작성
        - 너무 딱딱하거나 공식적이지 않게, 따뜻하고 자신감 있는 어조로 작성
        - 적절하게 예쁜 이모지를 섞어줘 (1~3개 정도)
        - 문장 끝에는 '함께 해요!', '지금 가입해보세요!' 같은 초대의 말투가 꼭 포함되어야 해
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
        아래는 %s 클럽의 %s 예산 데이터야.
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
                "오목회 테니스클럽", // 또는 dto에 clubName 필드 추가 시: dto.getClubName()
                dto.getMonth(),
                dto.getTotalIncome(),
                dto.getTotalExpense(),
                dto.getMembershipFee(),
                dto.getMemberCount(),
                dto.getFeePerMember(),
                categorySection.toString()
        );
    }
}


