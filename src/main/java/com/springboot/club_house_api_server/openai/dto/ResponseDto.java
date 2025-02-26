package com.springboot.club_house_api_server.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResponseDto {
    private List<Choice> choices;
    @Getter
    public static class Choice{
        private int index;
        private MessageDto message;
    }
}
