package com.paymybuddy.controller;


import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.RelationshipService;
import com.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
@RequestMapping("/relations")
public class RelationshipsController {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipsController.class);

    private final UserService userService;
    private final RelationshipService relationshipService;

    @Autowired
    public RelationshipsController(UserService userService, RelationshipService relationshipService) {
        this.userService = userService;
        this.relationshipService = relationshipService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Mes relations - Pay My Buddy");
    }


    @GetMapping
    public String getRelationships(HttpSession session, Model model) {

        Optional<UserSessionDTO> user = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        if (user.isEmpty()) return "redirect:/sign-in";

        return "/views/relationships";
    }

    @PostMapping
    public String getRelationships(
            @RequestParam String email,
            HttpSession session,
            Model model
    ) {

        Optional<UserSessionDTO> user = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        if (user.isEmpty()) return "redirect:/sign-in";

        Optional<User> receiver = userService.getUserByEmail(email);

        if (receiver.isEmpty()) {
            logger.warn("Aucun utilisateur trouvé avec l'adresse mail {}", email);
            model.addAttribute("email", email);
            model.addAttribute("errorMessage", "Aucun utilisateur trouvé avec cette adresse mail.");
            return "/views/relationships";
        }

        try {
            logger.info("Enregistrement de la demande d'ajout en cours pour : {}", email);

            relationshipService.addRelationship(user.get(), receiver.get());
            model.addAttribute("successMessage", "Votre demande d'ajout a bien été envoyée.");

            logger.info("Nouvelle demande d'ajout enregistrée avec succès pour : {}", email);
// ! Revoir les exceptions
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la demande d'ajout : {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la demande d'ajout : {}", e.getMessage());
            model.addAttribute("errorMessage", "Erreur lors de la demande d'ajout. Veuillez réessayer.");
        }

        return "/views/relationships";
    }
}
