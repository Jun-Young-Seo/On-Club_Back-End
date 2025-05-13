package com.springboot.club_house_api_server.openai.analyze.service;

import com.springboot.club_house_api_server.openai.analyze.dto.ClubDescriptionDto;
import com.springboot.club_house_api_server.openai.analyze.dto.CustomRequestDto;
import com.springboot.club_house_api_server.openai.analyze.dto.MessageDto;
import com.springboot.club_house_api_server.openai.analyze.dto.ResponseDto;
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


}
