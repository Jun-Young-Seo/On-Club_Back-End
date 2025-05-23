package com.springboot.club_house_api_server.excel.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.openai.analyze.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CategorizationService {
    private final OpenAIService openAIService;
    private static final int MAX_BATCH_SIZE = 30;
    //thread-safety
    private final Map<String,String> gptCache = new ConcurrentHashMap<>();

    public List<TransactionEntity> categorizeTransactions(List<TransactionEntity> transactions){
        List<String> descriptionForRequest = new ArrayList<>();
        Set<String> uniqueDescription = new HashSet<>();

        for(TransactionEntity transaction : transactions){
            String description = transaction.getTransactionDescription();

            if(description!=null && !description.isBlank()){
                if(!gptCache.containsKey(description) && !uniqueDescription.contains(description)){
                    descriptionForRequest.add(description);
                    uniqueDescription.add(description);
                }
            }
        }

        if(!descriptionForRequest.isEmpty()){
            Map<String,String> gptResult = requestCategorizeToGPT(descriptionForRequest);

            for(Map.Entry<String, String> entry : gptResult.entrySet()){
                gptCache.put(entry.getKey(), entry.getValue());
            }
        }
        for (TransactionEntity transaction : transactions) {
            String description = transaction.getTransactionDescription();
            if (description != null && gptCache.containsKey(description)) {
                String detail = gptCache.get(description);
//                System.out.println(description+" : "+detail);
                transaction.setTransactionDetail(detail);
            }
        }

        return transactions;

    }

    private Map<String, String> requestCategorizeToGPT(List<String> descriptions){
        Map<String,String> result = new HashMap<>();
        for(int i=0; i<descriptions.size(); i+=MAX_BATCH_SIZE){
            //마지막 배치인 경우를 위해 Math.min으로 크기 비교
            int end = Math.min(i+MAX_BATCH_SIZE, descriptions.size());
            //배치는 부분 리스트. 50개씩 나누어서 요청
            List<String> batch = descriptions.subList(i,end);

            String prompt = buildPrompt(batch);
            String gptResponse = openAIService.getGptResponse(prompt);
            Map<String,String> batchResult = parseGptResponse(gptResponse);

            result.putAll(batchResult);
        }
        return result;

    }

    private String buildPrompt(List<String> descriptions) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음 문장은 거래 내역이야. 거래 내역은 테니스 클럽의 통장 사용 내역이야.\n");
        sb.append("주로 모든 클럽에 있는 거래 내역은 다음과 같아.\n");
        sb.append("코트비, 코트비 환불, 회비, 회비 환불, 교통비, 용품비, 회식비, 이자");
        sb.append("사람 이름(2~4 글자)이라고 판단되면 회비로 처리해\n");
        sb.append("교통 수단의 이름이면 교통비, 식당의 이름이라면 회식비로 처리하자.\n");
        sb.append("또, 이자는 이자로 처리해줘.\n");
        sb.append("각 내용을 보고, 위의 분류에 가장 알맞은 것을 선택해줘\n");
        sb.append("하지만 판단하기에 애매하다면 ? 필드로 저장해. 무리해서 억지로 추론하지 말고 애매하면 ?로 해. 빈 칸으로 비우면 안돼 반드시 ?를 설정해\n\n");
        sb.append("각 항목은 순수한 JSON 형식으로 반환하고 \"다음은 ~~~입니다.\"와 같이 부연 설명은 붙이지 마.\n");
        sb.append("또한 추가적인 설명이나 마크다운 표기는 절대 포함되면 안돼.\n");
        sb.append("응답 예시는 다음과 같아:\n");
        sb.append("{ \"XX운수\": \"교통비\", \"XX식당\": \"식비\" }\n\n");
        sb.append("지정한 응답만을 제공할 수 있도록 해줘.\n");

        for (String desc : descriptions) {
            sb.append("- ").append(desc).append("\n");
        }

        return sb.toString();
    }
    private Map<String, String> parseGptResponse(String gptResponse) {
        Map<String, String> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(gptResponse, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
