package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final JavaMailSender emailSender;
    
    // Store OTP and expiration time
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiryTimeStorage = new HashMap<>();
    private static final int EXPIRATION_MINUTES = 2; // OTP valid for 2 minutes

    @Autowired
    public OtpService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    // Generate and send OTP to the user's email
    public void sendOtp(String username) {
        String otp = generateOtp();
        otpStorage.put(username, otp); 
        otpExpiryTimeStorage.put(username, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)); // Set expiration time

        sendEmail(username, otp); // Send OTP to user's email
    }

    // Verify the entered OTP and check expiration
    public boolean verifyOtp(String username, String otp) {
        if (otpStorage.containsKey(username)) {
            LocalDateTime expirationTime = otpExpiryTimeStorage.get(username);
            if (LocalDateTime.now().isBefore(expirationTime)) {
                return otp.equals(otpStorage.get(username)); // Verify OTP if not expired
            } else {
                otpStorage.remove(username); // Remove expired OTP
                otpExpiryTimeStorage.remove(username); // Remove expiration time
            }
        }
        return false; // Return false if OTP is invalid or expired
    }

    // Generate a 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private void sendEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp + ". It is valid for 2 minutes.");
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}