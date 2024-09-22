package com.paymybuddy.controller;


import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import com.paymybuddy.util.SessionUtil;
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


/**
 * Controller class for handling profile-related requests.
 */
@Controller
@RequestMapping("/profil")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private static final String PROFILE_VIEW = "views/profile";

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Adds common attributes to the model.
     *
     * @param model the model to add attributes to
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Mon profil - Pay My Buddy");
    }


    /**
     * Handles GET requests to the /profil endpoint.
     * Populates the model with user profile data.
     *
     * @param session the HTTP session
     * @param model the model to populate
     * @return the view name
     */
    @GetMapping
    public String profile(HttpSession session, Model model) {

        UserSessionDTO userSession = SessionUtil.getUserSession(session);

        UserProfileUpdateDTO user = new UserProfileUpdateDTO();
        user.setUsername(userSession.username());
        user.setEmail(userSession.email());

        model.addAttribute("user", user);

        return PROFILE_VIEW;
    }


    /**
     * Handles POST requests to the /profil endpoint.
     * Updates the user profile with the provided data.
     *
     * @param userUpdate the user profile update data
     * @param result the binding result for validation
     * @param model the model to populate
     * @param session the HTTP session
     * @return the view name
     */
    @PostMapping
    public String updateUserProfile(@ModelAttribute("user") @Valid UserProfileUpdateDTO userUpdate,
                                    BindingResult result,
                                    Model model,
                                    HttpSession session) {

        logger.info("Requête pour la modification d'un nouvel utilisateur : {}", userUpdate.getUsername());

        UserSessionDTO userSession = SessionUtil.getUserSession(session);

        userService.userVerification(userUpdate, result, userSession);

        if (result.hasErrors()) {
            logger.warn("Données utilisateur non valide pour la modification : {}", userUpdate.getUsername() + ", " + userUpdate.getEmail());
            return PROFILE_VIEW;
        }

        logger.info("Enregistrement de la modification de l'utilisateur en cours : {}", userUpdate.getUsername());

        User userUpdated = userService.updateUser(userUpdate, userSession.id());
        UserSessionDTO newUserSession = new UserSessionDTO(userUpdated.getId(), userUpdated.getUsername(), userUpdated.getEmail());
        session.setAttribute("user", newUserSession);

        model.addAttribute("successMessage", "Votre profil a été modifié avec succès.");

        logger.info("Modification de l'utilisateur enregistrée avec succès : {}", userUpdate.getUsername());

        return PROFILE_VIEW;
    }
}
