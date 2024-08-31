package com.paymybuddy.controller;


import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpSession;
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

import java.util.Optional;


@Controller
@RequestMapping("/profil")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Mon profil - Pay My Buddy");
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {

        Optional<UserSessionDTO> userSession = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        if (userSession.isEmpty()) return "redirect:/sign-in";

        UserProfileUpdateDTO user = new UserProfileUpdateDTO();
        user.setUsername(userSession.get().username());
        user.setEmail(userSession.get().email());

        model.addAttribute("user", user);

        return "/views/profile";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") @Valid UserProfileUpdateDTO userUpdate,
                               BindingResult result,
                               Model model,
                               HttpSession session) {

        logger.info("Requête pour la modification d'un nouvel utilisateur : {}", userUpdate.getUsername());

        Optional<UserSessionDTO> user = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        userService.userVerification(userUpdate, result, user.get());

        if (result.hasErrors()) {
            logger.warn("Données utilisateur non valide pour la modification : {}", userUpdate.getUsername() + ", " + userUpdate.getEmail());
            return "views/profile";
        }

        try {
            logger.info("Enregistrement de la modification de l'utilisateur en cours : {}", userUpdate.getUsername());

            User userUpdated = userService.updateUser(userUpdate, user.get().id());
            UserSessionDTO newUserSession = new UserSessionDTO(userUpdated.getId(), userUpdated.getUsername(), userUpdated.getEmail());
            session.setAttribute("user", newUserSession);

            model.addAttribute("successMessage", "Votre profil a été modifié avec succès.");

            logger.info("Modification de l'utilisateur enregistrée avec succès : {}", userUpdate.getUsername());

        } catch (Exception e) {
            logger.error("Erreur lors de la modification du compte : {}", e.getMessage());
            model.addAttribute("errorMessage", "Erreur lors de la modification de votre compte. Veuillez réessayer.");
        }

        return "views/profile";
    }
}
