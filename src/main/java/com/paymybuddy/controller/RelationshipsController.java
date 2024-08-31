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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    public void populateModel(Model model, HttpSession session) {
        Optional<UserSessionDTO> user = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        model.addAttribute("userRelations", relationshipService.getUserRelations(user.get().id()));
        model.addAttribute("waitingUserRelations", relationshipService.getWaitingUserRelations(user.get().id()));
    }


    @GetMapping
    public String getRelationships(HttpSession session, Model model) {

        Optional<UserSessionDTO> user = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        if (user.isEmpty()) return "redirect:/sign-in";

        populateModel(model, session);

        return "/views/relationships";
    }

    @GetMapping(params = {"accepted", "requesterId"})
    public String handleRelationshipAction(
            @RequestParam boolean accepted,
            @RequestParam long requesterId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        logger.info("Traitement de la mise à jour de la demande d'ajout pour : {}", requesterId);

        long userId = ((UserSessionDTO) session.getAttribute("user")).id();

        if (accepted) {
            try {
                logger.info("Acceptation de la demande d'ajout en cours pour : {}", requesterId);
                relationshipService.validateRelationship(requesterId, userId);
                redirectAttributes.addFlashAttribute("successMessage", "La demande a bien été validée.");
                logger.info("Demande d'ajout validée avec succès pour : {}", requesterId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Erreur lors de la validation de la demande d'ajout : {}", e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            }
            return "redirect:/relations";
        }

        try {
            logger.info("Refus de la demande d'ajout en cours pour : {}", requesterId);
            relationshipService.deleteRelationship(requesterId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "La demande a bien été supprimée.");
            logger.info("Demande d'ajout supprimée avec succès pour : {}", requesterId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Erreur lors de la suppression de la demande d'ajout : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/relations";
    }

    @GetMapping(params = {"deleteRelationship"})
    public String deleteRelationship(
            @RequestParam long deleteRelationship,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        logger.info("Suppression de la relation en cours pour : {}", deleteRelationship);

        long userId = ((UserSessionDTO) session.getAttribute("user")).id();

        try {
            logger.info("Suppression de la relation en cours pour : {}", deleteRelationship);
            relationshipService.deleteRelationship(userId, deleteRelationship);
            redirectAttributes.addFlashAttribute("successMessage", "La relation a bien été supprimée.");
            logger.info("Relation supprimée avec succès pour : {}", deleteRelationship);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Erreur lors de la suppression de la relation : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/relations";
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
            // ! REVOIR LES EXCEPTIONS
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la demande d'ajout : {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la demande d'ajout : {}", e.getMessage());
            model.addAttribute("errorMessage", "Erreur lors de la demande d'ajout. Veuillez réessayer.");
        }

        populateModel(model, session);

        return "/views/relationships";
    }
}
