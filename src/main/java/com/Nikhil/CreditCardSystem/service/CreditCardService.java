package com.Nikhil.CreditCardSystem.service;

import com.Nikhil.CreditCardSystem.Entity.CreditCard;
import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.Entity.Transaction;
import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
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

        logger.info("Credit card created successfully for customer ID: {}", customerId);

        ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
        structure.setMessage("Credit card created successfully");
//        structure.setHttpstatus(HttpStatus.CREATED.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(toDto(card));
        return ResponseEntity.status(HttpStatus.CREATED).body(structure);
    }

    // ✅ Update card details
    public ResponseEntity<ResponseStructure<CreditCardDto>> updateCard(Long cardId, CreditCardDto cardDto) {
        logger.info("Updating card ID: {}", cardId);

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        card.setCardHolderName(cardDto.getCardHolderName());
        creditCardRepository.save(card);

        logger.info("Card ID {} updated successfully", cardId);

        ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
        structure.setMessage("Credit card updated successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(toDto(card));
        return ResponseEntity.ok(structure);
    }

    // ✅ Get card by ID
    public ResponseEntity<ResponseStructure<CreditCardDto>> getCardById(Long cardId) {
        logger.info("Fetching card with ID: {}", cardId);

        CreditCardDto dto = toDto(creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found")));

        ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
        structure.setMessage("Credit card fetched successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(dto);
        return ResponseEntity.ok(structure);
    }

    // ✅ Get all cards of a customer
    public ResponseEntity<ResponseStructure<List<CreditCardDto>>> getCardsByCustomer(Long customerId) {
        logger.info("Fetching all cards for customer ID: {}", customerId);

        List<CreditCardDto> cards = creditCardRepository.findAllByCustomerId(customerId)
                .stream().map(this::toDto).toList();

        ResponseStructure<List<CreditCardDto>> structure = new ResponseStructure<>();
        structure.setMessage("All cards for customer fetched successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(cards);
        return ResponseEntity.ok(structure);
    }

    // ✅ Delete card
    public ResponseEntity<ResponseStructure<String>> deleteCard(Long cardId) {
        logger.warn("Deleting card ID: {}", cardId);

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        creditCardRepository.delete(card);

        logger.info("Card ID {} deleted successfully", cardId);

        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Credit card deleted successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData("Deleted ID: " + cardId);
        return ResponseEntity.ok(structure);
    }

    // ✅ Debit card
    public ResponseEntity<ResponseStructure<CreditCardDto>> debitCard(Long customerId, String cardNumber, double amount) {
        logger.info("Debiting ₹{} from card {} for customer ID {}", amount, cardNumber, customerId);

        CreditCard card = creditCardRepository.findByCardNumberAndCustomerId(cardNumber, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found for customer"));

        if (amount > card.getTotalBalance())
            throw new ValidationException("Insufficient balance");
        if (amount > card.getMAX_WITHDRAWAL_LIMIT())
            throw new ValidationException("Max withdrawal limit exceeded");
        if (card.getDailyDebitedAmount() + amount > card.getDAILY_DEBIT_LIMIT())
            throw new ValidationException("Daily debit limit exceeded");

        card.setTotalBalance(card.getTotalBalance() - amount);
        card.setDailyDebitedAmount(card.getDailyDebitedAmount() + amount);
        creditCardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEBIT");
        transaction.setCardType(card.getCardType());
        transaction.setDescription("Debited ₹" + amount);
        transaction.setCreditCard(card);
        transactionRepository.save(transaction);

        logger.info("Debit of ₹{} recorded for card {}", amount, cardNumber);

        ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
        structure.setMessage("Amount debited and transaction recorded");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(toDto(card));
        return ResponseEntity.ok(structure);
    }

    // ✅ Credit card
    public ResponseEntity<ResponseStructure<CreditCardDto>> creditCard(Long customerId, String cardNumber, double amount) {
        logger.info("Crediting ₹{} to card {} for customer ID {}", amount, cardNumber, customerId);

        CreditCard card = creditCardRepository.findByCardNumberAndCustomerId(cardNumber, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found for customer"));

        if (amount > card.getMAX_CREDIT_LIMIT()) {
            throw new ValidationException("Amount exceeds max credit limit");
        }


        if (card.getDailyCreditedAmount() + amount > card.getDAILY_CREDIT_LIMIT()) {
            throw new ValidationException("Daily credit limit exceeded");
        }

        card.setTotalBalance(card.getTotalBalance() + amount);
        card.setDailyCreditedAmount(card.getDailyCreditedAmount() + amount);
        creditCardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("CREDIT");
        transaction.setCardType(card.getCardType());
        transaction.setDescription("Credited ₹" + amount);
        transaction.setCreditCard(card);
        transactionRepository.save(transaction);

        logger.info("Credit of ₹{} recorded for card {}", amount, cardNumber);

        ResponseStructure<CreditCardDto> structure = new ResponseStructure<>();
        structure.setMessage("Amount credited and transaction recorded");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(toDto(card));
        return ResponseEntity.ok(structure);
    }

}
