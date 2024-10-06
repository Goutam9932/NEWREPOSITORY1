package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login"; // Return the login HTML page
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin_dashboard"; // Return the dashboard HTML page after login
    }
    
}
