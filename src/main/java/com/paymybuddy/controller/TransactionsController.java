package com.paymybuddy.controller;

import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.RelationshipService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller class for handling transaction-related requests.
 */
@Controller
@RequestMapping("/transactions")
public class TransactionsController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    private static final String TRANSACTIONS_VIEW = "views/transactions";

    private final TransactionService transactionService;
    private final RelationshipService relationshipService;
    private final AccountService accountService;

    @Autowired
    public TransactionsController(TransactionService transactionService, RelationshipService relationshipService, AccountService accountService) {
        this.transactionService = transactionService;
        this.relationshipService = relationshipService;
        this.accountService = accountService;
    }


    /**
     * Adds common attributes to the model.
     *
     * @param model the model to add attributes to
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Mes transactions - Pay My Buddy");
        model.addAttribute("js", "/js/transactions.js");
    }


    /**
     * Populates the model with user session data, account balance, relationships, and transactions.
     *
     * @param session the HTTP session
     * @param model the model to populate
     * @param page the page number for pagination
     */
    public void populateModel(HttpSession session, Model model, int page) {

        UserSessionDTO userSession = SessionUtil.getUserSession(session);

        accountService.getAccountByUserId(userSession.id()).ifPresent(account -> model.addAttribute("balance", account.getBalance()));
        List<UserRelationshipProjection> relationships = relationshipService.getUserRelations(userSession.id());

        Pageable pageable = PageRequest.of(page, 10);
        Page<TransactionProjection> transactionsPage = transactionService.getTransactions(userSession.id(), pageable);

        model.addAttribute("transactionsPage", transactionsPage);
        model.addAttribute("relationships", relationships);
    }


    /**
     * Handles GET requests to the /transactions endpoint.
     *
     * @param page the page number for pagination
     * @param session the HTTP session
     * @param model the model to populate
     * @return the view name
     */
    @GetMapping
    public String transactions(
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        SessionUtil.getUserSession(session);

        populateModel(session, model, page);

        model.addAttribute("Transaction", new Transaction());

        return TRANSACTIONS_VIEW;
    }


    /**
     * Handles POST requests to the /transactions endpoint.
     *
     * @param transaction the transaction to register
     * @param result the binding result for validation
     * @param session the HTTP session
     * @param model the model to populate
     * @return the view name
     */
    @PostMapping
    public String transactions(
            @ModelAttribute("Transaction") @Valid Transaction transaction,
            BindingResult result,
            HttpSession session,
            Model model
    ) {

        logger.info("Requête pour l'enregistrement d'une nouvelle transaction : {}", transaction);

        UserSessionDTO userSession = SessionUtil.getUserSession(session);

        if (result.hasErrors()) {
            logger.warn("Données de transaction non valide pour l'enregistrement : {}", transaction);
            populateModel(session, model, 0);
            return TRANSACTIONS_VIEW;
        }

        logger.info("Enregistrement de la transaction en cours : {}", transaction);

        transactionService.registerTransaction(userSession, transaction);
        model.addAttribute("successMessage", "Votre transaction a été effectuée avec succès.");

        logger.info("Nouvelle transaction enregistrée avec succès : {}", transaction);

        populateModel(session, model, 0);
        model.addAttribute("Transaction", new Transaction());

        return TRANSACTIONS_VIEW;
    }

}
