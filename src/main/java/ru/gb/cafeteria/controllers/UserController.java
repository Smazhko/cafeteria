package ru.gb.cafeteria.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class UserController {

    @GetMapping("/admin")
    public String goToAdminPage() {
        return "admin";
    }

    @GetMapping("/kitchen")
    public String goToKitchenPage() {
        return "kitchen";
    }

    @GetMapping("/management")
    public String goToFinancePage() {
        return "management";
    }

}
