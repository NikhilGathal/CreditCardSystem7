package com.Nikhil.CreditCardSystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDto {
    private Long id;
    private String username;
    private String name;
    private String phoneNumber;
    private String email;
    private List<CreditCardDto> creditCards;
}
