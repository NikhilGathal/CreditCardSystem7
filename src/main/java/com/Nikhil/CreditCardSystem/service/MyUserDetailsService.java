package com.Nikhil.CreditCardSystem.service;

import com.Nikhil.CreditCardSystem.Entity.Customer;
import com.Nikhil.CreditCardSystem.model.UserPrincipal;
import com.Nikhil.CreditCardSystem.repo.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;




@Service
public class MyUserDetailsService implements UserDetailsService{

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Customer customer  = customerRepository.findByUsername(username);
        if(customer == null)
        {
            System.out.println("User not found");
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(customer);
    }

}

