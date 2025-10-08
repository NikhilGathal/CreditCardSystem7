package com.Nikhil.CreditCardSystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    private double amount;
    private String transactionType;
    private String cardType;
    private String description;
    private LocalDateTime timestamp;
    private String cardNumber; // Optional: if you want to show card info
}

