    package com.springboot.club_house_api_server.club.account.dto;

    import lombok.*;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class ClubAccountDto {
        private long accountId;
        private Long clubId;
        private String accountName;
        private String accountNumber;
        private String accountOwner;
        private String bankName;
    }
