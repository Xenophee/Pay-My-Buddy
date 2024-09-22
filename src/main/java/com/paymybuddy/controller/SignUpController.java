package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller class for handling sign-up related requests.
 */
@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    private static final String SIGN_UP_VIEW = "views/sign-up";
    private static final String REDIRECT_AFTER_SIGN_UP = "redirect:/sign-in";

    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Adds common attributes to the model.
     *
     * @param model the model to add attributes to
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "S'inscrire - Pay My Buddy");
    }


    /**
     * Handles GET requests to the /sign-up endpoint.
     *
     * @param model the model to populate
     * @return the view name
     */
    @GetMapping
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return SIGN_UP_VIEW;
    }


    /**
     * Handles POST requests to the /sign-up endpoint.
     *
     * @param user the user to register
     * @param result the binding result for validation
     * @param redirectAttributes the redirect attributes for flash messages
     * @return the view name
     */
    @PostMapping
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        logger.info("Requête pour la création d'un nouvel utilisateur : {}", user.getUsername());

        userService.userVerification(user, result);

        if (result.hasErrors()) {
            logger.warn("Données utilisateur non valide pour l'enregistrement : {}", user.getUsername() + ", " + user.getEmail());
            return SIGN_UP_VIEW;
        }

        logger.info("Enregistrement du nouvel utilisateur en cours : {}", user.getUsername() + ", " + user.getEmail());

        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");

        logger.info("Nouvel utilisateur enregistré avec succès : {}", user.getUsername() + ", " + user.getEmail());

        return REDIRECT_AFTER_SIGN_UP;
    }
}
