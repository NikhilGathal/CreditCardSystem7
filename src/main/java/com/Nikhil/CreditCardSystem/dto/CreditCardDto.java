package com.Nikhil.CreditCardSystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditCardDto {
    private String cardNumber;
    private double totalBalance;
    private String cardHolderName;
    private String cardType;
    private boolean isActive;
    private LocalDate issueDate;
    private LocalDate expiryDate;

}
