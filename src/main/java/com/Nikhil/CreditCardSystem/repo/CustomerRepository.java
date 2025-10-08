package com.Nikhil.CreditCardSystem.repo;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer
        , Long> {


    Customer findByUsername(String username);
}
