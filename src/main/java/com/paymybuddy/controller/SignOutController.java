package com.paymybuddy.controller;


import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/sign-out")
public class SignOutController {

    private static final Logger logger = LoggerFactory.getLogger(SignOutController.class);

    @GetMapping
    public String signOut(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
