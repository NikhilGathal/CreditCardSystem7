package com.Nikhil.CreditCardSystem.controller;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
import com.Nikhil.CreditCardSystem.dto.CustomerDto;
import com.Nikhil.CreditCardSystem.model.CustomerModel;
import com.Nikhil.CreditCardSystem.service.CustomerService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    /**
     * 📋 API: Fetch all customers
     *
     * Endpoint: GET /api/customers
     * Description: Retrieves a list of all registered customers in the system.
     * Response: Returns a list of CustomerDto objects.
     */

    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<CustomerDto>>> fetchAllCustomers() {
        LOGGER.info("Fetching all customers");
        return customerService.getAllCustomers();
    }

    /**
     * 🔍 API: Get customer by ID
     *
     * Endpoint: GET /api/customers/{id}
     * Description: Retrieves a specific customer’s details based on their ID.
     * Path Variable: id (Long)
     * Response: Returns CustomerDto for the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<CustomerDto>> fetchCustomerById(@PathVariable Long id) {
        LOGGER.info("Fetching customer by ID: {}", id);
        return customerService.getCustomerById(id);
    }

    /**
     * ✏️ API: Update customer details
     *
     * Endpoint: PUT /api/customers/{id}/update
     * Description: Updates an existing customer's information.
     * Path Variable: id (Long)
     * Request Body: Customer (updated details)
     * Response: Returns updated CustomerDto.
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<ResponseStructure<CustomerDto>> updateCustomerDetails(
            @PathVariable Long id,
            @RequestBody Customer customer) {
        LOGGER.info("Updating customer with ID: {}", id);
        return customerService.updateCustomer(id, customer);
    }

    /**
     * ❌ API: Delete customer account
     *
     * Endpoint: DELETE /api/customers/{id}/delete
     * Description: Deletes a customer record based on their ID.
     * Path Variable: id (Long)
     * Response: Returns confirmation message after deletion.
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ResponseStructure<String>> deleteCustomerAccount(@PathVariable Long id) {
        LOGGER.info("Deleting customer with ID: {}", id);
        return customerService.deleteCustomer(id);
    }
}
