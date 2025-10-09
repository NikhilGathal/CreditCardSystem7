package com.Nikhil.CreditCardSystem.controller;


import com.Nikhil.CreditCardSystem.Entity.CreditCard;
import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.Entity.Transaction;
import com.Nikhil.CreditCardSystem.dto.TransactionDto;
import com.Nikhil.CreditCardSystem.exception.ResourceNotFoundException;
import com.Nikhil.CreditCardSystem.repo.CreditCardRepository;
import com.Nikhil.CreditCardSystem.repo.CustomerRepository;
import com.Nikhil.CreditCardSystem.service.TransactionService;
import com.Nikhil.CreditCardSystem.service.UserActionLogService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Autowired
    private UserActionLogService userActionLogService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final TransactionService transactionService;

    public TransactionController(CustomerRepository customerRepository,
                                 CreditCardRepository creditCardRepository,
                                 TransactionService transactionService) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.transactionService = transactionService;
    }

    /**
     * 📄 API: Get all transactions for a specific user (across all their credit cards)
     *
     * Endpoint: GET /api/transactions/user/{userId}
     * Description: Retrieves all transactions made by a user across all their credit cards.
     * Path Variable:
     *      - userId (Long): ID of the user
     * Response: List of TransactionDto objects.
     */

    // ✅ 1️⃣ Get all transactions for a user
    @Operation(
            summary = "Get all transactions for a specific user",
            description = "Retrieves all transactions made by a user across all their credit cards."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getAllTransactionsByUser(
            @Parameter(description = "User ID for which to fetch transactions", example = "1")
            @PathVariable Long userId) {

        LOGGER.info("Fetching all transactions for user ID: {}", userId);

        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        try {
            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            List<TransactionDto> transactionDtos = customer.getCreditCards().stream()
                    .flatMap(card -> card.getTransactions().stream())
                    .map(transactionService::toDto)
                    .collect(Collectors.toList());

            String message = transactionDtos.isEmpty()
                    ? "No transactions found for user ID: " + userId
                    : "Found " + transactionDtos.size() + " transactions for user ID: " + userId;

            response.setMessage(message);
            response.setHttpstatus("SUCCESS");
            response.setData(transactionDtos);

            // Use username from fetched customer entity
            userActionLogService.logAction(customer.getName(), "Fetch all transactions for userId " + userId, "SUCCESS");

        } catch (Exception e) {
            // Attempt to log failed action with userId as fallback
            String username = customerRepository.findById(userId)
                    .map(Customer::getName)
                    .orElse("Unknown");
            userActionLogService.logAction(username, "Fetch all transactions for userId " + userId, "FAILED");
            throw e;
        }

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

    // ✅ 2️⃣ Get all transactions for a credit card
    @Operation(
            summary = "Get all transactions for a specific credit card",
            description = "Retrieves all transactions linked to a specific credit card."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Credit card not found")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/card/{cardId}")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getTransactionsByCard(
            @Parameter(description = "Credit card ID to fetch transactions for", example = "101")
            @PathVariable Long cardId) {

        LOGGER.info("Fetching transactions for card ID: {}", cardId);
        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        try {
            CreditCard card = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
            List<TransactionDto> transactionDtos = card.getTransactions().stream()
                    .map(transactionService::toDto)
                    .collect(Collectors.toList());

            String message = transactionDtos.isEmpty()
                    ? "No transactions found for card ID: " + cardId
                    : "Found " + transactionDtos.size() + " transactions for card ID: " + cardId;

            response.setMessage(message);
            response.setHttpstatus("SUCCESS");
            response.setData(transactionDtos);

            // Get username from card owner
            userActionLogService.logAction(card.getCustomer().getName(), "Fetch transactions for cardId " + cardId, "SUCCESS");

        } catch (Exception e) {
            // Attempt to log failed action with cardId as fallback
            creditCardRepository.findById(cardId).ifPresent(card ->
                    userActionLogService.logAction(card.getCustomer().getName(), "Fetch transactions for cardId " + cardId, "FAILED"));
            throw e;
        }

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

    // ✅ 3️⃣ Get all CREDIT transactions for a user
    @Operation(
            summary = "Get all CREDIT transactions for a user",
            description = "Retrieves all credit transactions made by a user across all their credit cards."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credit transactions fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}/credits")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getAllCreditTransactionsByUser(
            @Parameter(description = "User ID for fetching credit transactions", example = "1")
            @PathVariable Long userId) {

        LOGGER.info("Fetching all CREDIT transactions for user ID: {}", userId);
        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        try {
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

            response.setMessage(message);
            response.setHttpstatus("SUCCESS");
            response.setData(creditTransactions);

            userActionLogService.logAction(customer.getName(), "Fetch CREDIT transactions for userId " + userId, "SUCCESS");

        } catch (Exception e) {
            String username = customerRepository.findById(userId)
                    .map(Customer::getName)
                    .orElse("Unknown");
            userActionLogService.logAction(username, "Fetch CREDIT transactions for userId " + userId, "FAILED");
            throw e;
        }

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

    // ✅ 4️⃣ Get all DEBIT transactions for a user
    @Operation(
            summary = "Get all DEBIT transactions for a user",
            description = "Retrieves all debit transactions made by a user across all their credit cards."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Debit transactions fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}/debits")
    public ResponseEntity<ResponseStructure<List<TransactionDto>>> getAllDebitTransactionsByUser(
            @Parameter(description = "User ID for fetching debit transactions", example = "1")
            @PathVariable Long userId) {

        LOGGER.info("Fetching all DEBIT transactions for user ID: {}", userId);
        ResponseStructure<List<TransactionDto>> response = new ResponseStructure<>();
        try {
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

            response.setMessage(message);
            response.setHttpstatus("SUCCESS");
            response.setData(debitTransactions);

            userActionLogService.logAction(customer.getName(), "Fetch DEBIT transactions for userId " + userId, "SUCCESS");

        } catch (Exception e) {
            String username = customerRepository.findById(userId)
                    .map(Customer::getName)
                    .orElse("Unknown");
            userActionLogService.logAction(username, "Fetch DEBIT transactions for userId " + userId, "FAILED");
            throw e;
        }

        return ResponseEntity.ok(response);
    }

}
