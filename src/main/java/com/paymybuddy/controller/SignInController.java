package com.paymybuddy.controller;

import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/sign-in")
public class SignInController {

    private static final Logger logger = LoggerFactory.getLogger(SignInController.class);

    private final UserService userService;

    @Autowired
    public SignInController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Se connecter - Pay My Buddy");
    }

    @GetMapping
    public String signIn(Model model) {
        return "views/sign-in";
    }

    @PostMapping
    public String signIn(@RequestParam String email,
                         @RequestParam String password,
                         Model model,
                         HttpSession session) {

        model.addAttribute("email", email);

        try {
            UserSessionDTO user = userService.authenticateUser(email, password);
            session.setAttribute("user", user);
            return "redirect:/transactions";

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "views/sign-in";
        }
    }

}
