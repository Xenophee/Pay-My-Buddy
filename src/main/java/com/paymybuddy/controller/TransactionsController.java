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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Mes transactions - Pay My Buddy");
    }

    private void populateModel(HttpSession session, Model model) {
        UserSessionDTO userSession = SessionUtil.getUserSession(session);
        accountService.getAccountByUserId(userSession.id()).ifPresent(account -> model.addAttribute("balance", account.getBalance() + " €"));
        List<UserRelationshipProjection> relationships = relationshipService.getUserRelations(userSession.id());
        List<TransactionProjection> transactions = transactionService.getTransactionsByUserIdWithUsernames(userSession.id());

        model.addAttribute("transactions", transactions);
        model.addAttribute("relationships", relationships);
    }


    @GetMapping
    public String transactions(HttpSession session, Model model) {

        SessionUtil.getUserSession(session);

        populateModel(session, model);

        model.addAttribute("Transaction", new Transaction());

        return TRANSACTIONS_VIEW;
    }


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
            populateModel(session, model);
            return TRANSACTIONS_VIEW;
        }

        logger.info("Enregistrement de la transaction en cours : {}", transaction);

        transactionService.registerTransaction(userSession, transaction);
        model.addAttribute("successMessage", "Votre transaction a été effectuée avec succès.");

        logger.info("Nouvelle transaction enregistrée avec succès : {}", transaction);

        populateModel(session, model);
        model.addAttribute("Transaction", new Transaction());

        return TRANSACTIONS_VIEW;
    }

}
