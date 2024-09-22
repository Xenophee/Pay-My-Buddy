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


/**
 * Controller class for handling sign-in related requests.
 */
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


    /**
     * Adds common attributes to the model.
     *
     * @param model the model to add attributes to
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Se connecter - Pay My Buddy");
    }


    /**
     * Handles GET requests to the /sign-in endpoint.
     *
     * @return the view name
     */
    @GetMapping
    public String signIn() {
        return SIGN_IN_VIEW;
    }


    /**
     * Handles POST requests to the /sign-in endpoint.
     * Authenticates the user and creates a session.
     *
     * @param email the user's email
     * @param password the user's password
     * @param session the HTTP session
     * @param model the model to populate
     * @return the redirect URL after sign-in
     */
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
