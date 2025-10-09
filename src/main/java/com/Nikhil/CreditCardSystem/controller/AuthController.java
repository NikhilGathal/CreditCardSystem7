package com.Nikhil.CreditCardSystem.controller;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.dto.CustomerDto;
import com.Nikhil.CreditCardSystem.model.CustomerModel;
import com.Nikhil.CreditCardSystem.service.CustomerService;
import com.Nikhil.CreditCardSystem.service.UserActionLogService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserActionLogService userActionLogService;

    /**
     * 🧾 API: Register a new customer
     *
     * Endpoint: POST /api/auth/register
     * Description: Creates a new customer account with provided details.
     * Request Body: Customer (name, username, email, password, phoneNumber, role, etc.)
     * Response: Returns the saved Customer details (CustomerDto).
     */
    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer account by accepting user details such as username, email, and password."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<CustomerDto>> registerCustomer(@RequestBody Customer customer) {
        LOGGER.info("Register API called for username: {}", customer.getUsername());
        try {
            ResponseEntity<ResponseStructure<CustomerDto>> response = customerService.createCustomer(customer);

            // Log action dynamically
            userActionLogService.logAction(
                    customer.getName(),
                    "Register new customer",
                    "SUCCESS"
            );

            return response;
        } catch (Exception e) {
            // Log failed attempt dynamically
            userActionLogService.logAction(
                    customer.getName(),
                    "Register new customer",
                    "FAILED"
            );
            throw e; // propagate exception
        }
    }

    /**
     * 🔐 API: Login existing customer
     *
     * Endpoint: POST /api/auth/login
     * Description: Authenticates a customer based on username and password.
     * Request Body: CustomerModel (username, password)
     * Response: Returns success message if valid credentials, otherwise error.
     */
    @Operation(
            summary = "Login a registered customer",
            description = "Validates customer credentials (username and password) and returns authentication status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<String>> loginCustomer(@RequestBody CustomerModel customerModel) {
        LOGGER.info("Login attempt for username: {}", customerModel.getUsername());
        try {
            ResponseEntity<ResponseStructure<String>> response = customerService.verify(customerModel);

            // Log successful login
            userActionLogService.logAction(
                    customerModel.getUsername(),
                    "Login attempt",
                    "SUCCESS"
            );

            return response;
        } catch (Exception e) {
            // Log failed login attempt
            userActionLogService.logAction(
                    customerModel.getUsername(),
                    "Login attempt",
                    "FAILED"
            );
            throw e; // propagate exception
        }
    }

}
