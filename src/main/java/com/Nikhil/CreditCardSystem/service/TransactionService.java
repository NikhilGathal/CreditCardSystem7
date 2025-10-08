package com.Nikhil.CreditCardSystem.service;

import com.Nikhil.CreditCardSystem.Entity.Transaction;
import com.Nikhil.CreditCardSystem.dto.TransactionDto;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public TransactionDto toDto(Transaction tx) {
        TransactionDto dto = new TransactionDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setTransactionType(tx.getTransactionType());
        dto.setCardType(tx.getCardType());
        dto.setDescription(tx.getDescription());
        dto.setTimestamp(tx.getDateTime());

        if (tx.getCreditCard() != null) {
            dto.setCardNumber(tx.getCreditCard().getCardNumber()); // optional
        }

        return dto;
    }

}
