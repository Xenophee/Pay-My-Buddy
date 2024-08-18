package com.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SignInController {

    @GetMapping("/sign-in")
    public String signIn(Model model) {
        model.addAttribute("title", "Se connecter - Pay My Buddy");
        return "views/sign-in";
    }
}
