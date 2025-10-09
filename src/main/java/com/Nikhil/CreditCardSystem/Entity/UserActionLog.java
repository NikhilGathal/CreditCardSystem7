package com.Nikhil.CreditCardSystem.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_action_logs")
@Data
public class UserActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Who performed the action
    private String action;   // Description of the action
    private String status;   // SUCCESS / FAILED
    private LocalDateTime actionTime = LocalDateTime.now();
}