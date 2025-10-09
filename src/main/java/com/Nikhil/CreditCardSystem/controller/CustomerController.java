package com.Nikhil.CreditCardSystem.controller;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
import com.Nikhil.CreditCardSystem.dto.CustomerDto;
import com.Nikhil.CreditCardSystem.model.CustomerModel;
import com.Nikhil.CreditCardSystem.service.CustomerService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    // 📋 FETCH ALL CUSTOMERS
    @Operation(
            summary = "Fetch all customers",
            description = "Retrieves a list of all registered customers in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customers fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<CustomerDto>>> fetchAllCustomers(Principal principal) {
        LOGGER.info("Fetching all customers");
        return customerService.getAllCustomers(principal);
    }


    /**
     * 🔍 API: Get customer by ID
     *
     * Endpoint: GET /api/customers/{id}
     * Description: Retrieves a specific customer’s details based on their ID.
     * Path Variable: id (Long)
     * Response: Returns CustomerDto for the given ID.
     */

    // 🔍 GET CUSTOMER BY ID
    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves a specific customer’s details based on their unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer details fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<CustomerDto>> fetchCustomerById(
            @Parameter(description = "Unique ID of the customer to fetch") @PathVariable Long id) {

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

    // ✏️ UPDATE CUSTOMER DETAILS
    @Operation(
            summary = "Update customer details",
            description = "Updates an existing customer's information based on the provided details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/update")
    public ResponseEntity<ResponseStructure<CustomerDto>> updateCustomerDetails(
            @Parameter(description = "Unique ID of the customer to update") @PathVariable Long id,
            @Parameter(description = "Customer object containing updated details") @RequestBody Customer customer) {

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
    // ❌ DELETE CUSTOMER
    @Operation(
            summary = "Delete customer account",
            description = "Deletes a customer record from the system based on their unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ResponseStructure<String>> deleteCustomerAccount(
            @Parameter(description = "Unique ID of the customer to delete") @PathVariable Long id) {

        LOGGER.info("Deleting customer with ID: {}", id);
        return customerService.deleteCustomer(id);
    }
}
