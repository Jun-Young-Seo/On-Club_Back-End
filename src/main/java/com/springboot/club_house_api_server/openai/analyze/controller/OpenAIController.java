package com.springboot.club_house_api_server.openai.analyze.controller;

import com.springboot.club_house_api_server.openai.analyze.dto.ClubDescriptionDto;
import com.springboot.club_house_api_server.openai.analyze.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openai")
public class OpenAIController {
    private final OpenAIService openAIService;

    @PostMapping("/analyze/budget")
    public ResponseEntity<?> analyzeBudgetExcel(){
       return null;
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody String prompt){
        String response = openAIService.getGptResponse(prompt);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/make/club_description")
    public ResponseEntity<?> makeClubDescription(@RequestBody ClubDescriptionDto dto){
        System.out.println(dto.toString());
        return openAIService.writeClubDescriptionWithAI(dto);
    }

}
