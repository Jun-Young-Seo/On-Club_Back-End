package com.springboot.club_house_api_server.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategorySummaryDto {
    private String category;
    private Long amount;

    @Override
    public String toString() {
        return "Category : " + category + ", Amount : " + amount;
    }
}
