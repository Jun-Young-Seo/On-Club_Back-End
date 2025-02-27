    package com.springboot.club_house_api_server.club.account.dto;

    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    public class ClubAccountDto {
        private Long clubId;
        private String accountName;
        private String accountNumber;
        private String accountOwner;
        private String bankName;
    }
