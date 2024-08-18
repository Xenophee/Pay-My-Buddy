package com.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SignUpController {

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("title", "S'inscrire - Pay My Buddy");
        return "views/sign-up";
    }
}
