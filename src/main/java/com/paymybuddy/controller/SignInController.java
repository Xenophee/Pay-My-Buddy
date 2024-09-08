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

    private static final String SIGN_IN_VIEW = "views/sign-in";
    private static final String REDIRECT_AFTER_SIGN_IN = "redirect:/transactions";

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
    public String signIn() {
        return SIGN_IN_VIEW;
    }

    @PostMapping
    public String signIn(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        logger.info("Requête de connexion pour l'utilisateur : {}", email);

        if (email.isEmpty() || password.isEmpty()) {
            model.addAttribute("errorMessage", "Veuillez renseigner tous les champs.");
            return SIGN_IN_VIEW;
        }

        UserSessionDTO user = userService.authenticateUser(email, password);

        logger.info("Utilisateur connecté : {}", email);

        session.setAttribute("user", user);

        logger.info("Session utilisateur créée : {}", session.getId());

        return REDIRECT_AFTER_SIGN_IN;
    }

}
