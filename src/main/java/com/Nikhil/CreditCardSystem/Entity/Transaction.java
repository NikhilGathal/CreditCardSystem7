package com.Nikhil.CreditCardSystem.Entity;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private String transactionType; // e.g. "DEBIT" or "CREDIT"
    private String cardType;
    private String description;
    private LocalDateTime dateTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "card_id")
    private CreditCard creditCard; // each transaction belongs to one card
}

