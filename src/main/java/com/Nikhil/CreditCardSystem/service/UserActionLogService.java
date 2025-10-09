package com.Nikhil.CreditCardSystem.service;


import com.Nikhil.CreditCardSystem.Entity.UserActionLog;
import com.Nikhil.CreditCardSystem.repo.UserActionLogRepository;
import org.springframework.stereotype.Service;

@Service
public class UserActionLogService {

    private final UserActionLogRepository logRepository;

    public UserActionLogService(UserActionLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // Single method to save log
    public void logAction(String username, String action, String status) {
        UserActionLog log = new UserActionLog();
        log.setName(username);
        log.setAction(action);
        log.setStatus(status);
        logRepository.save(log);
    }
}
