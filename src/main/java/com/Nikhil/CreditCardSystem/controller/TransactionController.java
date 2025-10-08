package com.Nikhil.CreditCardSystem.controller;


import com.Nikhil.CreditCardSystem.Entity.CreditCard;
import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.Entity.Transaction;
import com.Nikhil.CreditCardSystem.dto.TransactionDto;
import com.Nikhil.CreditCardSystem.exception.ResourceNotFoundException;
import com.Nikhil.CreditCardSystem.repo.CreditCardRepository;
import com.Nikhil.CreditCardSystem.repo.CustomerRepository;
import com.Nikhil.CreditCardSystem.service.TransactionService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    /**
     * 📄 API: Get all transactions for a specific user (across all their credit cards)
     *
     * Endpoint: GET /api/transactions/user/{userId}
     * Description: Retrieves all transactions made by a user across all their credit cards.
     * Path Variable:
     *      - userId (Long): ID of the user
     * Response: List of TransactionDto objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getAllTransactionsByUser(@PathVariable Long userId) {
        LOGGER.info("Fetching all transactions for user ID: {}", userId);

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User not found");
                });

        List<TransactionDto> transactionDtos = customer.getCreditCards().stream()
                .flatMap(card -> card.getTransactions().stream())
                .map(transactionService::toDto)
                .collect(Collectors.toList());

        LOGGER.info("Found {} transactions for user ID: {}", transactionDtos.size(), userId);

        String message ;
        if (transactionDtos.isEmpty()) {
             message = "No transactions found for user ID: " + userId;
        } else {
             message = "Found " + transactionDtos.size() + " transactions for user ID: " + userId;
        }

        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        response.setMessage("Transactions fetched successfully " + message);
//        response.setHttpstatus(HttpStatus.OK.value());
        response.setHttpstatus("SUCCESS");
        response.setData(transactionDtos);

        return ResponseEntity.ok(response);
    }

    /**
     * 📄 API: Get all transactions for a specific credit card
     *
     * Endpoint: GET /api/transactions/card/{cardId}
     * Description: Retrieves all transactions linked to a specific credit card.
     * Path Variable:
     *      - cardId (Long): ID of the credit card
     * Response: List of TransactionDto objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/card/{cardId}")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getTransactionsByCard(@PathVariable Long cardId) {
        LOGGER.info("Fetching transactions for card ID: {}", cardId);

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    LOGGER.warn("Card with ID {} not found", cardId);
                    return new ResourceNotFoundException("Card not found");
                });

        List<TransactionDto> transactionDtos = card.getTransactions().stream()
                .map(transactionService::toDto)
                .collect(Collectors.toList());

        LOGGER.info("Found {} transactions for card ID: {}", transactionDtos.size(), cardId);


        String message;
        if (transactionDtos.isEmpty()) {
            message = "Found 0 transactions for user ID: " + cardId;
        } else {
            message = "Found " + transactionDtos.size() + " transactions for user ID: " + cardId;
        }


        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        response.setMessage("Transactions fetched successfully " + message);
//        response.setHttpstatus(HttpStatus.OK.value());
        response.setHttpstatus("SUCCESS");
        response.setData(transactionDtos);

        return ResponseEntity.ok(response);
    }

    /**
     * 📄 API: Get all CREDIT transactions for a specific user (across all their credit cards)
     *
     * Endpoint: GET /api/transactions/user/{userId}/credits
     * Description: Retrieves all credit transactions made by a user across all their credit cards.
     * Path Variable:
     *      - userId (Long): ID of the user
     * Response: List of TransactionDto objects filtered by type "CREDIT".
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}/credits")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getAllCreditTransactionsByUser(@PathVariable Long userId) {
        LOGGER.info("Fetching all CREDIT transactions for user ID: {}", userId);

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<TransactionDto> creditTransactions = customer.getCreditCards().stream()
                .flatMap(card -> card.getTransactions().stream())
                .filter(txn -> "CREDIT".equalsIgnoreCase(txn.getTransactionType()))
                .map(transactionService::toDto)
                .collect(Collectors.toList());

        String message = creditTransactions.isEmpty()
                ? "No credit transactions found for user ID: " + userId
                : "Found " + creditTransactions.size() + " credit transactions for user ID: " + userId;

        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        response.setMessage(message);
        response.setHttpstatus("SUCCESS");
        response.setData(creditTransactions);

        return ResponseEntity.ok(response);
    }


    /**
     * 📄 API: Get all DEBIT transactions for a specific user (across all their credit cards)
     *
     * Endpoint: GET /api/transactions/user/{userId}/debits
     * Description: Retrieves all debit transactions made by a user across all their credit cards.
     * Path Variable:
     *      - userId (Long): ID of the user
     * Response: List of TransactionDto objects filtered by type "DEBIT".
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}/debits")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getAllDebitTransactionsByUser(@PathVariable Long userId) {
        LOGGER.info("Fetching all DEBIT transactions for user ID: {}", userId);

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<TransactionDto> debitTransactions = customer.getCreditCards().stream()
                .flatMap(card -> card.getTransactions().stream())
                .filter(txn -> "DEBIT".equalsIgnoreCase(txn.getTransactionType()))
                .map(transactionService::toDto)
                .collect(Collectors.toList());

        String message = debitTransactions.isEmpty()
                ? "No debit transactions found for user ID: " + userId
                : "Found " + debitTransactions.size() + " debit transactions for user ID: " + userId;

        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        response.setMessage(message);
        response.setHttpstatus("SUCCESS");
        response.setData(debitTransactions);

        return ResponseEntity.ok(response);
    }

}
