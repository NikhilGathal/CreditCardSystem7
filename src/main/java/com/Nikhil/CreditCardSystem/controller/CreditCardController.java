package com.Nikhil.CreditCardSystem.controller;


import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
import com.Nikhil.CreditCardSystem.service.CreditCardService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // 🆕 CREATE CREDIT CARD
    @Operation(
            summary = "Create a new credit card for a customer",
            description = "Creates a new credit card linked to a specific customer with an initial balance, type, and status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credit card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID or balance"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ResponseStructure<CreditCardDto>> createCreditCard(
            @Parameter(description = "ID of the customer to whom the credit card belongs") @RequestParam Long customerId,
            @Parameter(description = "Initial balance for the credit card") @RequestParam double balance,
            @Parameter(description = "Type of the credit card (e.g., VISA, MasterCard)") @RequestParam String type,
            @Parameter(description = "Indicates whether the card is active or not") @RequestParam boolean isactive) {

        LOGGER.info("Creating credit card for customerId: {}, balance: {}, type: {}, isActive: {}", customerId, balance, type, isactive);
        return cardService.createCard(customerId, balance, type, isactive);
    }

    /**
     * 🔍 API: Get credit card by ID
     *
     * Endpoint: GET /api/creditcards/{cardId}
     * Description: Retrieves the details of a specific credit card based on its ID.
     * Path Variable: cardId (Long)
     * Response: Returns CreditCardDto for the given card ID.
     */

    // 🔍 GET CREDIT CARD BY ID
    @Operation(
            summary = "Get credit card by ID",
            description = "Fetches the details of a specific credit card using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credit card details fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Credit card not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{cardId}")
    public ResponseEntity<ResponseStructure<CreditCardDto>> getCreditCardById(
            @Parameter(description = "Unique ID of the credit card") @PathVariable Long cardId) {

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

    // 📋 GET ALL CARDS FOR A CUSTOMER
    @Operation(
            summary = "Get all credit cards for a specific customer",
            description = "Retrieves all credit cards associated with a given customer ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credit cards fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found or no cards available"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ResponseStructure<List<CreditCardDto>>> getCreditCardsByCustomer(
            @Parameter(description = "Customer ID for which to retrieve credit cards") @PathVariable Long customerId) {

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

    // ✏️ UPDATE CREDIT CARD DETAILS
    @Operation(
            summary = "Update credit card details",
            description = "Updates the complete information of a credit card (not just balance) using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credit card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Credit card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{cardId}")
    public ResponseEntity<ResponseStructure<CreditCardDto>> updateCard(
            @Parameter(description = "Unique ID of the credit card to update") @PathVariable Long cardId,
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
    // ❌ DELETE CREDIT CARD
    @Operation(
            summary = "Delete a credit card",
            description = "Deletes a credit card permanently using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credit card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Credit card not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ResponseStructure<String>> deleteCreditCard(
            @Parameter(description = "Unique ID of the credit card to delete") @PathVariable Long cardId) {

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

    // 💸 DEBIT CREDIT CARD
    @Operation(
            summary = "Debit an amount from a credit card",
            description = "Deducts a specific amount from a customer's credit card balance."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amount debited successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient balance or invalid amount"),
            @ApiResponse(responseCode = "404", description = "Credit card not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/debit")
    public ResponseEntity<ResponseStructure<CreditCardDto>> debitCreditCard(
            @Parameter(description = "ID of the customer performing the transaction") @RequestParam Long customerId,
            @Parameter(description = "Credit card number from which to debit the amount") @RequestParam String cardNumber,
            @Parameter(description = "Amount to be debited") @RequestParam double amount) {

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

    // 💰 CREDIT CREDIT CARD
    @Operation(
            summary = "Credit an amount to a credit card",
            description = "Adds a specific amount to a customer's credit card balance."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amount credited successfully"),
            @ApiResponse(responseCode = "404", description = "Credit card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/credit")
    public ResponseEntity<ResponseStructure<CreditCardDto>> creditCreditCard(
            @Parameter(description = "ID of the customer performing the transaction") @RequestParam Long customerId,
            @Parameter(description = "Credit card number to which the amount will be credited") @RequestParam String cardNumber,
            @Parameter(description = "Amount to be credited") @RequestParam double amount) {

        LOGGER.info("Crediting ₹{} to cardNumber: {}, for customerId: {}", amount, cardNumber, customerId);
        return cardService.creditCard(customerId, cardNumber, amount);
    }
}
