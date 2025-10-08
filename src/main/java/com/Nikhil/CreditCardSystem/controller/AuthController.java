package com.Nikhil.CreditCardSystem.controller;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.dto.CustomerDto;
import com.Nikhil.CreditCardSystem.model.CustomerModel;
import com.Nikhil.CreditCardSystem.service.CustomerService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
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

    /**
     * 🧾 API: Register a new customer
     *
     * Endpoint: POST /api/customers/register
     * Description: Creates a new customer account with provided details.
     * Request Body: Customer (name,username, email, password,email,phoneNumber,role etc.)
     * Response: Returns the saved Customer details (CustomerDto).
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<CustomerDto>> registerCustomer(@RequestBody Customer customer) {
        LOGGER.info("Register API called for username: {}", customer.getUsername());
        return customerService.createCustomer(customer);
    }

    /**
     * 🔐 API: Login existing customer
     *
     * Endpoint: POST /api/customers/login
     * Description: Authenticates a customer based on username and password.
     * Request Body: CustomerModel (username, password)
     * Response: Returns success message if valid credentials, otherwise error.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<String>> loginCustomer(@RequestBody CustomerModel customerModel) {
        LOGGER.info("Login attempt for username: {}", customerModel.getUsername());
        return customerService.verify(customerModel);
    }


}
