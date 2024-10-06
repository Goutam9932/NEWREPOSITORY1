package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();  // Plain text password for now
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/admin/**").authenticated()
            .and()
                .formLogin()
                    .loginPage("/login")
                    .failureHandler((request, response, exception) -> {
                        String email = request.getParameter("username");

                        // Check if the user has exceeded login attempts (both failed and successful)
                        if (userDetailsServiceImpl.isBlockedFromLogins(email)) {
                            response.sendRedirect("/login?error=loginLimitExceeded"); // Block after exceeding limit
                        } else {
                            // Increase login attempts and redirect to error page
                            userDetailsServiceImpl.increaseLoginAttempt(email);
                            response.sendRedirect("/login?error=true");
                        }
                    })

                    .successHandler((request, response, authentication) -> {
                        String email = authentication.getName();

                        // Check if user has exceeded login limit
                        if (userDetailsServiceImpl.isBlockedFromLogins(email)) {
                            response.sendRedirect("/login?error=loginLimitExceeded"); // Block after exceeding limit
                        } else {
                            // Reset attempts on successful login if needed
                            userDetailsServiceImpl.increaseLoginAttempt(email);  // Count this login as an attempt
                            otpService.sendOtp(email);  // Send OTP
                            response.sendRedirect("/otp");
                        }
                    })
                    .permitAll()
            .and()
                .logout()
                    .permitAll();
        return http.build();
    }

}
