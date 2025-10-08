package com.Nikhil.CreditCardSystem.Entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Nikhil.util.EncryptDecryptConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = org.Nikhil.util.EncryptDecryptConverter.class)
    @Column(unique = true)
    private String cardNumber; // unique per card

    private String cardHolderName;

    private LocalDate issueDate ;
    private LocalDate expiryDate ;

    private String cardType; // Optional (Visa, MasterCard)

    private boolean isActive = true; // card enabled by default

    private double totalBalance;
    private double dailyDebitedAmount = 0;
    private double dailyCreditedAmount = 0;

    private final double MAX_WITHDRAWAL_LIMIT = 50000;
    private final double DAILY_DEBIT_LIMIT = 20000;
    private final double DAILY_CREDIT_LIMIT = 50000;
    private final double MAX_CREDIT_LIMIT = 50000;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
}

