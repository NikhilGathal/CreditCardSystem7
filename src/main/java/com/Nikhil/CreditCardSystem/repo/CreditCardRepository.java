package com.Nikhil.CreditCardSystem.repo;

import com.Nikhil.CreditCardSystem.Entity.CreditCard;
import com.Nikhil.CreditCardSystem.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByCardNumberAndCustomerId(String cardNumber, Long customerId);

    List<CreditCard> findAllByCustomerId(Long customerId);
    boolean existsByCardNumber(String cardNumber);
}
