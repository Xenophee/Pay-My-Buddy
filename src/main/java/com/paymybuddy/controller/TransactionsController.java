package com.paymybuddy.controller;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.RelationshipService;
import com.paymybuddy.service.TransactionService;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionsController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

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
        UserSessionDTO user = (UserSessionDTO) session.getAttribute("user");
        accountService.getAccountByUserId(user.id()).ifPresent(account -> model.addAttribute("balance", account.getBalance() + " €"));
        List<UserRelationshipProjection> relationships = relationshipService.getUserRelations(user.id());
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserIdWithUsernames(user.id());

        model.addAttribute("transactions", transactions);
        model.addAttribute("relationships", relationships);
    }

    @GetMapping
    public String transactions(HttpSession session, Model model) {

        Optional<UserSessionDTO> userSessionDTO = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        if (userSessionDTO.isEmpty()) return "redirect:/sign-in";

        populateModel(session, model);

        model.addAttribute("Transaction", new Transaction());

        return "views/transactions";
    }


    @PostMapping
    public String transactions(
            @ModelAttribute("Transaction") @Valid Transaction transaction,
            BindingResult result,
            HttpSession session,
            Model model
            ) {

        logger.info("Requête pour l'enregistrement d'une nouvelle transaction : {}", transaction);

        Optional<UserSessionDTO> userSession = Optional.ofNullable((UserSessionDTO) session.getAttribute("user"));

        if (userSession.isEmpty()) return "redirect:/sign-in";

        if (result.hasErrors()) {
            logger.warn("Données de transaction non valide pour l'enregistrement : {}", transaction);
            populateModel(session, model);
            return "views/transactions";
        }

        try {
            logger.info("Enregistrement de la transaction en cours : {}", transaction);

            transactionService.registerTransaction(userSession.get(), transaction);
            model.addAttribute("successMessage", "Votre transaction a été effectuée avec succès.");

            logger.info("Nouvelle transaction enregistrée avec succès : {}", transaction);

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la transaction : {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de de la transaction : {}", e.getMessage());
            model.addAttribute("errorMessage", "Erreur lors de l'enregistrement de la transaction. Veuillez réessayer.");
        }

        populateModel(session, model);
        model.addAttribute("Transaction", new Transaction());

        return "views/transactions";
    }

}
