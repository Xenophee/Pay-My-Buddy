package com.paymybuddy.controller;


import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.RelationshipService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


/**
 * Controller class for handling relationship-related requests.
 */
@Controller
@RequestMapping("/relations")
public class RelationshipsController {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipsController.class);

    private static final String RELATIONSHIPS_VIEW = "views/relationships";
    private static final String RELATIONSHIPS_REDIRECT = "redirect:/relations";

    private final UserService userService;
    private final RelationshipService relationshipService;

    @Autowired
    public RelationshipsController(UserService userService, RelationshipService relationshipService) {
        this.userService = userService;
        this.relationshipService = relationshipService;
    }

    /**
     * Adds common attributes to the model.
     *
     * @param model the model to add attributes to
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Mes relations - Pay My Buddy");
    }


    /**
     * Populates the model with user relationships.
     *
     * @param model the model to populate
     * @param session the HTTP session
     */
    public void populateModel(Model model, HttpSession session) {
        UserSessionDTO userSession = SessionUtil.getUserSession(session);

        model.addAttribute("userRelations", relationshipService.getUserRelations(userSession.id()));
        model.addAttribute("waitingUserRelations", relationshipService.getWaitingUserRelations(userSession.id()));
    }


    /**
     * Handles GET requests to the /relations endpoint.
     *
     * @param session the HTTP session
     * @param model the model to populate
     * @return the view name
     */
    @GetMapping
    public String getRelationships(HttpSession session, Model model) {

        SessionUtil.getUserSession(session);

        populateModel(model, session);

        return RELATIONSHIPS_VIEW;
    }


    /**
     * Handles DELETE requests to remove a relationship.
     *
     * @param deleteRelationship the ID of the relationship to delete
     * @param session the HTTP session
     * @param redirectAttributes the redirect attributes for flash messages
     * @return the redirect URL after deletion
     */
    @DeleteMapping(params = {"deleteRelationship"})
    public String deleteRelationship(
            @RequestParam long deleteRelationship,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        logger.info("Requête pour la suppression d'une relation : {}", deleteRelationship);

        long userId = SessionUtil.getUserSession(session).id();

        logger.info("Suppression de la relation en cours pour : {}", deleteRelationship);

        relationshipService.deleteRelationship(userId, deleteRelationship);
        redirectAttributes.addFlashAttribute("successMessage", "La relation a bien été supprimée.");

        logger.info("Relation supprimée avec succès pour : {}", deleteRelationship);

        return RELATIONSHIPS_REDIRECT;
    }


    /**
     * Handles PUT requests to accept a waiting relationship.
     *
     * @param requesterId the ID of the user who requested the relationship
     * @param session the HTTP session
     * @param redirectAttributes the redirect attributes for flash messages
     * @return the redirect URL after acceptance
     */
    @PutMapping("/accept")
    public String acceptWaitingRelationship(
            @RequestParam long requesterId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        logger.info("Requête pour l'acceptation d'une demande d'ajout : {}", requesterId);

        long userId = SessionUtil.getUserSession(session).id();

        logger.info("Acceptation de la demande en cours pour : {}", requesterId);

        relationshipService.validateRelationship(requesterId, userId);
        redirectAttributes.addFlashAttribute("successMessage", "La demande a bien été validée.");

        logger.info("Demande d'ajout validée avec succès pour : {}", requesterId);

        return RELATIONSHIPS_REDIRECT;
    }


    /**
     * Handles DELETE requests to remove a waiting relationship.
     *
     * @param requesterId the ID of the user who requested the relationship
     * @param session the HTTP session
     * @param redirectAttributes the redirect attributes for flash messages
     * @return the redirect URL after deletion
     */
    @DeleteMapping("/delete")
    public String deleteWaitingRelationship(
            @RequestParam long requesterId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        logger.info("Requête pour le refus d'une demande d'ajout : {}", requesterId);

        long userId = SessionUtil.getUserSession(session).id();

        logger.info("Refus de la demande en cours pour : {}", requesterId);

        relationshipService.deleteRelationship(requesterId, userId);
        redirectAttributes.addFlashAttribute("successMessage", "La demande a bien été supprimée.");

        logger.info("Demande d'ajout supprimée avec succès pour : {}", requesterId);

        return RELATIONSHIPS_REDIRECT;
    }


    /**
     * Handles POST requests to add a new relationship.
     *
     * @param email the email of the user to add
     * @param session the HTTP session
     * @param model the model to populate
     * @return the view name
     */
    @PostMapping
    public String addRelationship(
            @RequestParam String email,
            HttpSession session,
            Model model
    ) {

        UserSessionDTO userSession = SessionUtil.getUserSession(session);

        logger.info("Requête pour l'ajout d'une nouvelle relation : {}", email);

        Optional<User> receiver = userService.getUserByEmail(email);

        if (receiver.isEmpty()) {
            logger.warn("Aucun utilisateur trouvé avec l'adresse mail {}", email);
            model.addAttribute("email", email);
            model.addAttribute("errorMessage", "Aucun utilisateur trouvé avec cette adresse mail.");
            populateModel(model, session);
            return RELATIONSHIPS_VIEW;
        }

        logger.info("Enregistrement de la demande d'ajout en cours pour : {}", email);

        relationshipService.addRelationship(userSession, receiver.get());
        model.addAttribute("successMessage", "Votre demande d'ajout a bien été envoyée.");

        logger.info("Nouvelle demande d'ajout enregistrée avec succès pour : {}", email);

        populateModel(model, session);

        return RELATIONSHIPS_VIEW;
    }
}
