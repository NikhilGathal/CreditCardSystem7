package com.Nikhil.CreditCardSystem.Entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Nikhil.util.EncryptDecryptConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = org.Nikhil.util.EncryptDecryptConverter.class)
    private String username;
    private String password;
    private String name;
    @Convert(converter = org.Nikhil.util.EncryptDecryptConverter.class)
    private String phoneNumber;
    @Convert(converter = org.Nikhil.util.EncryptDecryptConverter.class)
    private String email;

    private String role;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditCard> creditCards = new ArrayList<>();

}
