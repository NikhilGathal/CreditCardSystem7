package com.Nikhil.CreditCardSystem.service;

import java.util.Optional;
import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.dto.CreditCardDto;
import com.Nikhil.CreditCardSystem.dto.CustomerDto;
import com.Nikhil.CreditCardSystem.exception.ResourceNotFoundException;
import com.Nikhil.CreditCardSystem.model.CustomerModel;
import com.Nikhil.CreditCardSystem.repo.CustomerRepository;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CreditCardService cardService;



    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private CustomerDto toDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setUsername(customer.getUsername());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmail(customer.getEmail());
        dto.setCreditCards(customer.getCreditCards().stream().map(card -> {
            CreditCardDto cardDto = new CreditCardDto();
            cardDto.setCardNumber(card.getCardNumber());
            cardDto.setCardHolderName(card.getCardHolderName());
            cardDto.setTotalBalance(card.getTotalBalance());
            cardDto.setCardType(card.getCardType());
            cardDto.setActive(card.isActive());
            cardDto.setIssueDate(card.getIssueDate());
            cardDto.setExpiryDate(card.getExpiryDate());
            return cardDto;
        }).toList());
        return dto;
    }

    public ResponseEntity<ResponseStructure<CustomerDto>> createCustomer(Customer customer) {
        logger.info("Creating new customer with username: {}", customer.getUsername());
        customer.setPassword(encoder.encode(customer.getPassword()));
        CustomerDto dto = toDto(customerRepository.save(customer));
        logger.info("Customer created successfully with ID: {}", dto.getId());

        ResponseStructure<CustomerDto> structure = new ResponseStructure<>();
        structure.setMessage("Customer created successfully");
//        structure.setHttpstatus(HttpStatus.CREATED.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(structure);
    }

    public ResponseEntity<ResponseStructure<CustomerDto>> updateCustomer(Long id, Customer customerDetails) {
        logger.info("Updating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", id);
                    return new ResourceNotFoundException("Customer not found");
                });

        customer.setName(customerDetails.getName());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        customer.setEmail(customerDetails.getEmail());
        customer.setUsername(customerDetails.getUsername());
        customer.setPassword(customerDetails.getPassword());

        CustomerDto dto = toDto(customerRepository.save(customer));
        logger.info("Customer with ID {} updated successfully", id);

        ResponseStructure<CustomerDto> structure = new ResponseStructure<>();
        structure.setMessage("Customer updated successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(dto);
        return ResponseEntity.ok(structure);
    }

    public ResponseEntity<ResponseStructure<CustomerDto>> getCustomerById(Long id) {
        logger.info("Fetching customer by ID: {}", id);
        CustomerDto dto = toDto(customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", id);
                    return new ResourceNotFoundException("Customer not found");
                }));

        logger.info("Customer with ID {} fetched successfully", id);

        ResponseStructure<CustomerDto> structure = new ResponseStructure<>();
        structure.setMessage("Customer fetched successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(dto);
        return ResponseEntity.ok(structure);
    }

    public ResponseEntity<ResponseStructure<List<CustomerDto>>> getAllCustomers() {
        logger.info("Fetching all customers...");
        List<CustomerDto> dtoList = customerRepository.findAll().stream().map(this::toDto).toList();
        logger.info("Fetched {} customers successfully", dtoList.size());

        ResponseStructure<List<CustomerDto>> structure = new ResponseStructure<>();
        structure.setMessage("All customers fetched successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData(dtoList);
        return ResponseEntity.ok(structure);
    }

    public ResponseEntity<ResponseStructure<String>> deleteCustomer(Long id) {
        logger.info("Deleting customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found for deletion", id);
                    return new ResourceNotFoundException("Customer not found");
                });

        customerRepository.delete(customer);
        logger.info("Customer with ID {} deleted successfully", id);

        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Customer deleted successfully");
//        structure.setHttpstatus(HttpStatus.OK.value());
        structure.setHttpstatus("SUCCESS");
        structure.setData("Deleted ID: " + id);
        return ResponseEntity.ok(structure);
    }

    public ResponseEntity<ResponseStructure<String>> verify(CustomerModel customerModel) {
        logger.info("Attempting login for username: {}", customerModel.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(customerModel.getUsername(), customerModel.getPassword()));

        ResponseStructure<String> structure = new ResponseStructure<>();

        if (authentication.isAuthenticated()) {
            Customer customer = customerRepository.findByUsername(customerModel.getUsername());
            if (customer == null) {
                logger.error("User {} not found during login verification", customerModel.getUsername());
                throw new RuntimeException("User not found");
            }

            String token = jwtService.generateToken(customerModel.getUsername(), customer.getRole());
            logger.info("Login successful for user: {}", customerModel.getUsername());

            structure.setMessage("Login successful");
//            structure.setHttpstatus(HttpStatus.OK.value());
            structure.setHttpstatus("SUCCESS");
            structure.setData(token);
            return ResponseEntity.ok(structure);
        } else {
            logger.warn("Invalid login attempt for username: {}", customerModel.getUsername());
            structure.setMessage("Invalid credentials");
//            structure.setHttpstatus(HttpStatus.UNAUTHORIZED.value());
            structure.setHttpstatus("ERROR");
            structure.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(structure);
        }
    }
}