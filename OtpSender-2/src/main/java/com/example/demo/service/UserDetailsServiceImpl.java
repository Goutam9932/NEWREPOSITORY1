package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Map to store total login attempts per user
    private Map<String, Integer> loginAttempts = new HashMap<>();
    
    private final int MAX_LOGIN_ATTEMPTS = 3;  // Limit of total login attempts

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    // Increase login attempts (count both failed and successful)
    public void increaseLoginAttempt(String email) {
        int attempts = loginAttempts.getOrDefault(email, 0);
        attempts++;
        loginAttempts.put(email, attempts);
    }

    // Get total login attempts
    public int getLoginAttempts(String email) {
        return loginAttempts.getOrDefault(email, 0);
    }

    // Reset login attempts
    public void resetLoginAttempts(String email) {
        loginAttempts.put(email, 0);
    }

    // Check if the user is blocked after exceeding the total login limit
    public boolean isBlockedFromLogins(String email) {
        return getLoginAttempts(email) >= MAX_LOGIN_ATTEMPTS;
    }
}
