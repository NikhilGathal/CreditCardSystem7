package com.Nikhil.CreditCardSystem.service;

import com.Nikhil.CreditCardSystem.Entity.CreditCard;
import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.Entity.Transaction;
import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
import com.Nikhil.CreditCardSystem.exception.InvalidInputException;
import com.Nikhil.CreditCardSystem.exception.ResourceNotFoundException;
import com.Nikhil.CreditCardSystem.exception.ValidationException;
import com.Nikhil.CreditCardSystem.repo.CreditCardRepository;
import com.Nikhil.CreditCardSystem.repo.CustomerRepository;
import com.Nikhil.CreditCardSystem.repo.TransactionRepository;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CreditCardService {

    @Autowired
    private UserActionLogService userActionLogService;

    private static final Logger logger = LoggerFactory.getLogger(CreditCardService.class);

    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;



    public CreditCardService(CustomerRepository customerRepository,
                             CreditCardRepository creditCardRepository,
                             TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.transactionRepository = transactionRepository;
    }

    // ✅ Generate unique card number
    private String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = String.valueOf(1_0000_0000_0000_000L + new Random().nextLong(9_0000_0000_0000_000L));
        } while (creditCardRepository.existsByCardNumber(cardNumber));

        logger.info("Generated unique card number: {}", cardNumber);
        return cardNumber;
    }

    // ✅ Convert Entity to DTO
    private CreditCardDto toDto(CreditCard card) {
        CreditCardDto dto = new CreditCardDto();
        dto.setCardHolderName(card.getCardHolderName());
        dto.setActive(card.isActive());
        dto.setCardType(card.getCardType());
        dto.setCardNumber(card.getCardNumber());
        dto.setTotalBalance(card.getTotalBalance());
        dto.setIssueDate(card.getIssueDate());
        dto.setExpiryDate(card.getExpiryDate());
        return dto;
    }

    // ✅ Create card for customer
    public ResponseEntity<ResponseStructure<CreditCardDto>> createCard(Long customerId, double balance ,String type , boolean isactive) {
        try {
            // ✅ 1. Validate input
            if (customerId == null || customerId <= 0) {
                throw new ValidationException("Customer ID must be provided and greater than 0");
            }
            if (balance == 0 || balance <= 0) {
                throw new ValidationException("Balance must be greater than 0");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new ValidationException("Card type must not be empty");
            }

            // ✅ 2. Proceed normally
            logger.info("Creating card for customer ID: {}", customerId);
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

            CreditCard card = new CreditCard();
            card.setCardNumber(generateUniqueCardNumber());
            card.setTotalBalance(balance);
            card.setCardType(type);
            card.setActive(isactive);
            card.setIssueDate(LocalDate.now());
            card.setCardHolderName(customer.getName());
            card.setExpiryDate(LocalDate.now().plusYears(10));
            card.setCustomer(customer);

            customer.getCreditCards().add(card);
            customerRepository.save(customer);

            userActionLogService.logAction(customer.getName(), "Create Credit Card", "SUCCESS");

            ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
            structure.setMessage("Credit card created successfully");
            structure.setHttpstatus("SUCCESS");
            structure.setData(toDto(card));

            return ResponseEntity.status(HttpStatus.CREATED).body(structure);

        } catch (Exception ex) {
            logger.error("Error while creating card for customer ID {}: {}", customerId, ex.getMessage());

            // Try to get customer name only if available
            Customer customer = customerRepository.findById(customerId).get();
                  String  username = customer.getName();
            userActionLogService.logAction(username, "Create Credit Card", "FAILED: " + ex.getMessage());
            throw ex; // rethrow for @RestControllerAdvice
        }
    }

    // ✅ Update card details
    public ResponseEntity<ResponseStructure<CreditCardDto>> updateCard(Long cardId, CreditCardDto cardDto) {
        try {
            logger.info("Updating card ID: {}", cardId);

            // Input validation
            if (cardDto == null || cardDto.getCardHolderName() == null || cardDto.getCardHolderName().trim().isEmpty()) {
                throw new InvalidInputException("Card holder name cannot be null or empty");
            }

            // Find the card
            CreditCard card = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

            // Update fields
            card.setCardHolderName(cardDto.getCardHolderName());
            creditCardRepository.save(card);

            logger.info("Card ID {} updated successfully", cardId);

            // Log user action as SUCCESS
            userActionLogService.logAction(
                    card.getCustomer().getName(),
                    "Update Card ID " + cardId,
                    "SUCCESS"
            );

            // Prepare response
            ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
            structure.setMessage("Credit card updated successfully");
            structure.setHttpstatus("SUCCESS");
            structure.setData(toDto(card));

            return ResponseEntity.ok(structure);

        } catch (Exception ex) {
            logger.error("Error while updating card ID {}: {}", cardId, ex.getMessage());

            // Try to log with username if card exists, otherwise fallback
            CreditCard card = creditCardRepository.findById(cardId).get();
            String username = card.getCustomer().getName();
            userActionLogService.logAction(username, "Update Card ID " + cardId, "FAILED: " + ex.getMessage());

            throw ex; // rethrow for global exception handler
        }
    }

    // ✅ Get card by ID
    public ResponseEntity<ResponseStructure<CreditCardDto>> getCardById(Long cardId) {
        try {
            logger.info("Fetching card with ID: {}", cardId);

            // Try to find card
            CreditCard card = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

            // Convert to DTO
            CreditCardDto dto = toDto(card);

            // Log success action
            userActionLogService.logAction(
                    card.getCustomer().getName(),
                    "Get Card By ID " + cardId,
                    "SUCCESS"
            );

            // Prepare response
            ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
            structure.setMessage("Credit card fetched successfully");
            structure.setHttpstatus("SUCCESS");
            structure.setData(dto);

            return ResponseEntity.ok(structure);

        } catch (Exception ex) {
            logger.error("Error while fetching card ID {}: {}", cardId, ex.getMessage());

            // Attempt to get username if available
            CreditCard card = creditCardRepository.findById(cardId).get();
            String username = card.getCustomer().getName();


            // Log failure
            userActionLogService.logAction(username, "Get Card By ID " + cardId, "FAILED: " + ex.getMessage());

            // Rethrow so the global exception handler can handle it properly
            throw ex;
        }
    }

    // ✅ Get all cards of a customer
    public ResponseEntity<ResponseStructure<List<CreditCardDto>>> getCardsByCustomer(Long customerId) {
        try {
            logger.info("Fetching all cards for customer ID: {}", customerId);

            // Validate input
            if (customerId == null || customerId <= 0) {
                throw new InvalidInputException("Customer ID must be a positive number and cannot be null");
            }

            // Fetch customer (or throw exception)
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

            // Fetch all cards for this customer
            List<CreditCardDto> cards = creditCardRepository.findAllByCustomerId(customerId)
                    .stream()
                    .map(this::toDto)
                    .toList();

            logger.info("Fetched {} cards for customer ID: {}", cards.size(), customerId);

            // Log success action
            userActionLogService.logAction(
                    customer.getName(),
                    "Fetch All Cards for Customer ID " + customerId,
                    "SUCCESS"
            );

            // Build response
            ResponseStructure<List<CreditCardDto>> structure = new ResponseStructure<>();
            structure.setMessage("All cards for customer fetched successfully");
            structure.setHttpstatus("SUCCESS");
            structure.setData(cards);

            return ResponseEntity.ok(structure);

        } catch (Exception ex) {
            logger.error("Error while fetching cards for customer ID {}: {}", customerId, ex.getMessage());

            // Try to get username if available
            Customer customer = customerRepository.findById(customerId).get();
            String username = customer.getName();
            // Log failure action
            userActionLogService.logAction(
                    username,
                    "Fetch All Cards for Customer ID " + customerId,
                    "FAILED: " + ex.getMessage()
            );

            // Rethrow so your @RestControllerAdvice handles it
            throw ex;
        }
    }

    // ✅ Delete card
    public ResponseEntity<ResponseStructure<String>> deleteCard(Long cardId) {
        try {
            logger.warn("Attempting to delete card ID: {}", cardId);

            // ✅ Validate input
            if (cardId == null || cardId <= 0) {
                throw new InvalidInputException("Card ID must be a positive number and cannot be null");
            }

            // ✅ Find card or throw exception
            CreditCard card = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));

            // ✅ Delete the card
            creditCardRepository.delete(card);

            logger.info("Card ID {} deleted successfully", cardId);

            // ✅ Log success action
            userActionLogService.logAction(
                    card.getCustomer().getName(),
                    "Delete Card ID " + cardId,
                    "SUCCESS"
            );

            // ✅ Build response
            ResponseStructure<String> structure = new ResponseStructure<>();
            structure.setMessage("Credit card deleted successfully");
            structure.setHttpstatus("SUCCESS");
            structure.setData("Deleted ID: " + cardId);

            return ResponseEntity.ok(structure);

        } catch (Exception ex) {
            logger.error("Error while deleting card ID {}: {}", cardId, ex.getMessage());

            // ✅ Try to log with customer name if possible
            String username =creditCardRepository.findById(cardId).get().getCustomer().getName();

            // ✅ Log failure action
            userActionLogService.logAction(
                    username,
                    "Delete Card ID " + cardId,
                    "FAILED: " + ex.getMessage()
            );

            // ✅ Rethrow exception so global handler catches it
            throw ex;
        }
    }

    // ✅ Debit card
    public ResponseEntity<ResponseStructure<CreditCardDto>> debitCard(Long customerId, String cardNumber, double amount) {
        try {
            logger.info("Debiting ₹{} from card {} for customer ID {}", amount, cardNumber, customerId);

            // ✅ Input validation
            if (customerId == null || customerId <= 0) {
                throw new InvalidInputException("Customer ID must be valid and greater than zero");
            }
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                throw new InvalidInputException("Card number cannot be null or empty");
            }
            if (amount <= 0) {
                throw new InvalidInputException("Amount must be greater than zero");
            }

            // ✅ Find card
            CreditCard card = creditCardRepository.findByCardNumberAndCustomerId(cardNumber, customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found for customer"));

            // ✅ Business validation
            if (amount > card.getTotalBalance()) {
                throw new ValidationException("Insufficient balance");
            }
            if (amount > card.getMAX_WITHDRAWAL_LIMIT()) {
                throw new ValidationException("Max withdrawal limit exceeded");
            }
            if (card.getDailyDebitedAmount() + amount > card.getDAILY_DEBIT_LIMIT()) {
                throw new ValidationException("Daily debit limit exceeded");
            }

            // ✅ Perform debit
            card.setTotalBalance(card.getTotalBalance() - amount);
            card.setDailyDebitedAmount(card.getDailyDebitedAmount() + amount);
            creditCardRepository.save(card);

            // ✅ Record transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setTransactionType("DEBIT");
            transaction.setCardType(card.getCardType());
            transaction.setDescription("Debited ₹" + amount);
            transaction.setCreditCard(card);
            transactionRepository.save(transaction);

            logger.info("Debit of ₹{} recorded successfully for card {}", amount, cardNumber);

            // ✅ Log user action (SUCCESS)
            userActionLogService.logAction(
                    card.getCustomer().getName(),
                    "Debit ₹" + amount + " from Card " + cardNumber + " (Customer ID " + customerId + ")",
                    "SUCCESS"
            );

            // ✅ Response
            ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
            structure.setMessage("Amount debited and transaction recorded successfully");
            structure.setHttpstatus("SUCCESS");
            structure.setData(toDto(card));

            return ResponseEntity.ok(structure);

        } catch (Exception ex) {
            logger.error("Error while debiting ₹{} from card {} for customer ID {}: {}", amount, cardNumber, customerId, ex.getMessage());

            // ✅ Determine username for failed log (if possible)
            String username = customerRepository.findById(customerId).get().getName();


            // ✅ Log user action (FAILED)
            userActionLogService.logAction(
                    username,
                    "Debit ₹" + amount + " from Card " + cardNumber + " (Customer ID " + customerId + ")",
                    "FAILED: " + ex.getMessage()
            );

            // ✅ Rethrow so global exception handler captures it
            throw ex;
        }
    }

    // ✅ Credit card
    public ResponseEntity<ResponseStructure<CreditCardDto>> creditCard(Long customerId, String cardNumber, double amount) {
        logger.info("Crediting ₹{} to card {} for customer ID {}", amount, cardNumber, customerId);

        try {
            CreditCard card = creditCardRepository.findByCardNumberAndCustomerId(cardNumber, customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found for customer"));

            if (amount > card.getMAX_CREDIT_LIMIT()) {
                throw new ValidationException("Amount exceeds max credit limit");
            }

            if (card.getDailyCreditedAmount() + amount > card.getDAILY_CREDIT_LIMIT()) {
                throw new ValidationException("Daily credit limit exceeded");
            }

            // ✅ Perform credit operation
            card.setTotalBalance(card.getTotalBalance() + amount);
            card.setDailyCreditedAmount(card.getDailyCreditedAmount() + amount);
            creditCardRepository.save(card);

            // ✅ Save transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setTransactionType("CREDIT");
            transaction.setCardType(card.getCardType());
            transaction.setDescription("Credited ₹" + amount);
            transaction.setCreditCard(card);
            transactionRepository.save(transaction);

            logger.info("Credit of ₹{} recorded for card {}", amount, cardNumber);

            // ✅ Log successful action
            userActionLogService.logAction(
                    card.getCustomer().getName(),
                    "Credit ₹" + amount + " to Card " + cardNumber + " (Customer ID " + customerId + ")",
                    "SUCCESS"
            );

            ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
            structure.setMessage("Amount credited and transaction recorded");
            structure.setHttpstatus("SUCCESS");
            structure.setData(toDto(card));

            return ResponseEntity.ok(structure);

        } catch (ValidationException | ResourceNotFoundException ex) {
            logger.error("Credit operation failed for card {}: {}", cardNumber, ex.getMessage());

            Customer customer = customerRepository.findById(customerId).get();
            // ✅ Log failure
            userActionLogService.logAction(
                    customer.getName(), // or handle properly if you can extract customer name
                    "Failed Credit ₹" + amount + " to Card " + cardNumber,
                    "FAILURE: " + ex.getMessage()
            );

            throw ex; // rethrow to be handled by @RestControllerAdvice
        } catch (Exception ex) {
            logger.error("Unexpected error during credit operation: {}", ex.getMessage());
            Customer customer = customerRepository.findById(customerId).get();
            // ✅ Log unexpected error
            userActionLogService.logAction(
                    customer.getName(),
                    "Unexpected error while crediting ₹" + amount + " to Card " + cardNumber,
                    "ERROR: " + ex.getMessage()
            );

            throw new RuntimeException("Something went wrong during credit operation", ex);
        }
    }
}
