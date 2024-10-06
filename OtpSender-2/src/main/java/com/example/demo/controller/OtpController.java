package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Correct import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.service.OtpService;

@Controller
@RequestMapping("/otp")
public class OtpController {

	@Autowired
	private OtpService otpService; // Create an OTP service to handle OTP logic

	@GetMapping
	public ModelAndView showOtpPage() {
		return new ModelAndView("otp"); // Return the OTP view
	}

	@PostMapping
	public String verifyOtp(@RequestParam String otp, Authentication authentication) {
		if (otpService.verifyOtp(authentication.getName(), otp)) {
			return "admin_dashboard"; // Redirect to dashboard if OTP is correct
		}
		return "redirect:/otp?error=true"; // Redirect back to OTP page with error
	}

	@PostMapping("/send")
	public String sendOtp(Authentication authentication) {
		otpService.sendOtp(authentication.getName()); // Send OTP to the user's email
		return "OTP sent"; // Could return some status
	}
}
