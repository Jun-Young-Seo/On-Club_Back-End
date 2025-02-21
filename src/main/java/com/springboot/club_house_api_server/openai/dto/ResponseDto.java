package com.springboot.club_house_api_server.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private List<Choice> choices;

    public static class Choice{
        private int index;
        private MessageDto message;
    }
}
