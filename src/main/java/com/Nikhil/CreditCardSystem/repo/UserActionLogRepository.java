package com.Nikhil.CreditCardSystem.repo;

import com.Nikhil.CreditCardSystem.Entity.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
    // No custom methods needed for basic save
}
