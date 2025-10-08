package com.Nikhil.CreditCardSystem.repo;


import com.Nikhil.CreditCardSystem.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

