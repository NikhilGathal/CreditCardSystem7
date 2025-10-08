package com.Nikhil.CreditCardSystem.controller;


import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
import com.Nikhil.CreditCardSystem.service.CreditCardService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/creditcards")
public class CreditCardController {

    @Autowired
    private CreditCardService cardService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardController.class);

    /**
     * 🆕 API: Create a new credit card for a customer
     *
     * Endpoint: POST /api/creditcards?customerId={customerId}&balance={balance}
     * Description: Creates a new credit card linked to a specific customer with an initial balance.
     * Request Parameters:
     *      - customerId (Long): ID of the customer
     *      - balance (double): Initial balance of the card
     * Response: Returns the created CreditCardDto object.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ResponseStructure<CreditCardDto>> createCreditCard(
            @RequestParam Long customerId,
            @RequestParam double balance,@RequestParam String type,@RequestParam boolean isactive) {
        LOGGER.info("Creating credit card for customerId: {}, with balance: {}", customerId, balance ,type ,isactive);
        return cardService.createCard(customerId, balance ,type ,isactive);
    }

    /**
     * 🔍 API: Get credit card by ID
     *
     * Endpoint: GET /api/creditcards/{cardId}
     * Description: Retrieves the details of a specific credit card based on its ID.
     * Path Variable: cardId (Long)
     * Response: Returns CreditCardDto for the given card ID.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{cardId}")
    public ResponseEntity<ResponseStructure<CreditCardDto>> getCreditCardById(@PathVariable Long cardId) {
        LOGGER.info("Fetching credit card with cardId: {}", cardId);
        return cardService.getCardById(cardId);
    }

    /**
     * 📋 API: Get all credit cards for a specific customer
     *
     * Endpoint: GET /api/creditcards/customer/{customerId}
     * Description: Retrieves all credit cards associated with a given customer.
     * Path Variable: customerId (Long)
     * Response: Returns a list of CreditCardDto objects.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ResponseStructure<List<CreditCardDto>>> getCreditCardsByCustomer(@PathVariable Long customerId) {
        LOGGER.info("Fetching all credit cards for customerId: {}", customerId);
        return cardService.getCardsByCustomer(customerId);
    }

    /**
     * ✏️ API: Update credit card details (whole card, not just balance)
     *
     * Endpoint: PUT /api/creditcards/{cardId}
     * Description: Updates the details of an existing credit card (not just balance).
     * Path Variable: cardId (Long)
     * Request Body: CardDto with updated fields
     * Response: Returns updated CreditCardDto.
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{cardId}")
    public ResponseEntity<ResponseStructure<CreditCardDto>> updateCard(
            @PathVariable Long cardId,
            @RequestBody CreditCardDto cardDto) {
        LOGGER.info("Updating credit card with cardId: {}", cardId);
        return cardService.updateCard(cardId, cardDto);
    }

    /**
     * ❌ API: Delete a credit card
     *
     * Endpoint: DELETE /api/creditcards/{cardId}
     * Description: Deletes a credit card based on its ID.
     * Path Variable: cardId (Long)
     * Response: Returns confirmation message after deletion.
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ResponseStructure<String>> deleteCreditCard(@PathVariable Long cardId) {
        LOGGER.info("Deleting credit card with cardId: {}", cardId);
        return cardService.deleteCard(cardId);
    }

    /**
     * 💸 API: Debit an amount from a credit card
     *
     * Endpoint: POST /api/creditcards/debit?customerId={customerId}&cardNumber={cardNumber}&amount={amount}
     * Description: Deducts a specified amount from the given customer's credit card.
     * Request Parameters:
     *      - customerId (Long)
     *      - cardNumber (String)
     *      - amount (double)
     * Response: Returns updated CreditCardDto with new balance.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/debit")
    public ResponseEntity<ResponseStructure<CreditCardDto>> debitCreditCard(
            @RequestParam Long customerId,
            @RequestParam String cardNumber,
            @RequestParam double amount) {
        LOGGER.info("Debiting ₹{} from cardNumber: {}, for customerId: {}", amount, cardNumber, customerId);
        return cardService.debitCard(customerId, cardNumber, amount);
    }

    /**
     * 💰 API: Credit an amount to a credit card
     *
     * Endpoint: POST /api/creditcards/credit?customerId={customerId}&cardNumber={cardNumber}&amount={amount}
     * Description: Adds a specified amount to the given customer's credit card.
     * Request Parameters:
     *      - customerId (Long)
     *      - cardNumber (String)
     *      - amount (double)
     * Response: Returns updated CreditCardDto with new balance.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/credit")
    public ResponseEntity<ResponseStructure<CreditCardDto>> creditCreditCard(
            @RequestParam Long customerId,
            @RequestParam String cardNumber,
            @RequestParam double amount) {
        LOGGER.info("Crediting ₹{} to cardNumber: {}, for customerId: {}", amount, cardNumber, customerId);
        return cardService.creditCard(customerId, cardNumber, amount);
    }
}
