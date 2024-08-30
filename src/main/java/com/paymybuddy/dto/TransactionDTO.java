package com.paymybuddy.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    String username;
    boolean isSender;
    LocalDateTime processedAt;
    Double amount;
    String description;

    public TransactionDTO(String username, boolean isSender, LocalDateTime processedAt, Double amount, String description) {
        this.username = username;
        this.isSender = isSender;
        this.processedAt = processedAt;
        this.amount = amount;
        this.description = description;
    }
}
