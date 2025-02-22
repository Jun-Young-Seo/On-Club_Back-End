package com.springboot.club_house_api_server.openai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {
    @PostMapping("/analyze/budget")
    public ResponseEntity<?> analyzeBudgetExcel(){
       return null;
    }


}
