package com.Nikhil.CreditCardSystem.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securtiFilterChain(HttpSecurity http) throws Exception	{


        // below is builder pattern

        return http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request->request
                        .requestMatchers("/api/auth/register", "/api/auth/login",
                                "/creditcards/save/**","swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/favicon.ico",
                                "/webjars/**" )
                        .permitAll()
                        .requestMatchers("/api/creditcards/**").hasAnyRole("USER", "ADMIN")    // ✅ only ADMIN
                        .requestMatchers("/api/customers/**" ).hasRole("USER") // ✅ both
                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions().sameOrigin())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    // because above one is returing default one
    // above code again we are using the hardcoded values only but we want it to come from
    // data base for that  below code

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // without encoding password
//		provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        // with encoding password that is for authentication purpose user entered password is encrypted and
        // then compared it with the data base values
        // after this it will not work for normal username and password which are stored without any
        // authentications ex navin n@123 ask to update the password
        // below code is for bcrypting the password before we are
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return  config.getAuthenticationManager();
    }
}

