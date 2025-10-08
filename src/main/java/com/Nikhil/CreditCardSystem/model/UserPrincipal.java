package com.Nikhil.CreditCardSystem.model;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails{

    private Customer customer;



    public UserPrincipal( Customer customer) {
        super();
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + customer.getRole().toUpperCase()));
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return customer.getUsername();
    }

}

