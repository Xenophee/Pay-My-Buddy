package com.paymybuddy.controller;


import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller class for handling sign-out related requests.
 */
@Controller
@RequestMapping("/sign-out")
public class SignOutController {

    private static final Logger logger = LoggerFactory.getLogger(SignOutController.class);

    private static final String REDIRECT_AFTER_SIGN_OUT = "redirect:/";


    /**
     * Handles GET requests to the /sign-out endpoint.
     * Invalidates the current user session and redirects to the home page.
     *
     * @param session the HTTP session to invalidate
     * @return the redirect URL after sign-out
     */
    @GetMapping
    public String signOut(HttpSession session) {
        logger.info("Déconnexion de l'utilisateur en cours : {}", session.getAttribute("user"));
        session.invalidate();
        return REDIRECT_AFTER_SIGN_OUT;
    }
}
