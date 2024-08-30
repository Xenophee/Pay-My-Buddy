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


@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "S'inscrire - Pay My Buddy");
    }


    @GetMapping
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "views/sign-up";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        logger.info("Requête pour la création d'un nouvel utilisateur : {}", user.getUsername());

        userService.userVerification(user, result);

        if (result.hasErrors()) {
            logger.warn("Données utilisateur non valide pour l'enregistrement : {}", user.getUsername() + ", " + user.getEmail());
            return "views/sign-up";
        }

        try {
            logger.info("Enregistrement du nouvel utilisateur en cours : {}", user.getUsername());

            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");

            logger.info("Nouvel utilisateur enregistré avec succès : {}", user.getUsername());

            return "redirect:/sign-in";

        } catch (Exception e) {
            logger.error("Erreur lors de la création de compte : {}", e.getMessage());
            model.addAttribute("errorMessage", "Erreur lors de la création de votre compte. Veuillez réessayer.");
            return "views/sign-up";
        }
    }
}
