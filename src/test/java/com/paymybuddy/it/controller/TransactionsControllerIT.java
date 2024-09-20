package com.paymybuddy.it.controller;


import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SignInException;
import com.paymybuddy.exception.TransactionsException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.RelationshipService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private RelationshipService relationshipService;

    @MockBean
    private AccountService accountService;


    private static final String URL_TRANSACTIONS = "/transactions";
    private static final String VIEW_TRANSACTIONS = "views/transactions";
    private static final String REDIRECT_URL_NO_SESSION = "/sign-in";


    @Test
    @DisplayName("GET /transactions - succès")
    public void testGetTransactions() throws Exception {
        // Créer un objet UserSessionDTO pour simuler l'utilisateur connecté
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        // Simule la récupération du compte associé à l'utilisateur
        when(accountService.getAccountByUserId(userSession.id())).thenReturn(Optional.of(mock(Account.class)));

        // Simule la récupération des relations utilisateur
        when(relationshipService.getUserRelations(userSession.id())).thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(eq(userSession.id()), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Exécute la requête GET /transactions avec une session utilisateur
        mockMvc.perform(get(URL_TRANSACTIONS)
                        .sessionAttr("user", userSession))  // Session utilisateur injectée ici
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_TRANSACTIONS))
                .andExpect(model().attributeExists("title", "js", "transactionsPage", "relationships", "Transaction"));

        // Vérifie que les méthodes du service ont été appelées correctement
        verify(accountService).getAccountByUserId(userSession.id());
        verify(relationshipService).getUserRelations(userSession.id());
        verify(transactionService).getTransactions(eq(userSession.id()), any(Pageable.class));
    }


    @Test
    @DisplayName("GET /transactions - échec en l'absence de session utilisateur")
    public void testGetTransactionsNoSession() throws Exception {

        // Simule une requête sans session utilisateur
        mockMvc.perform(get(URL_TRANSACTIONS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_URL_NO_SESSION));

        // Vérifie que les services ne sont pas appelés
        verifyNoInteractions(accountService);
        verifyNoInteractions(relationshipService);
        verifyNoInteractions(transactionService);
    }

    @Test
    @DisplayName("Enregistrement d'une nouvelle transaction - succès")
    public void testPostTransactionSuccess() throws Exception {
        // Simuler l'utilisateur connecté
        UserSessionDTO userSession = new UserSessionDTO(1, "testUser", "test@example.com");

        // Créer un compte simulé avec un solde
        Account account = new Account();
        account.setBalance(100.0);

        // Simuler un compte utilisateur avec solde
        when(accountService.getAccountByUserId(userSession.id())).thenReturn(Optional.of(account));

        // Simuler des relations utilisateur
        when(relationshipService.getUserRelations(userSession.id())).thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(eq(userSession.id()), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Simuler une transaction réussie
        doNothing().when(transactionService).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));

        // Soumettre une nouvelle transaction avec des données valides
        mockMvc.perform(post(URL_TRANSACTIONS)
                        .param("amount", "50.0")
                        .param("description", "Payment for services")
                        .param("receiver.id", "2")  // Récepteur de la transaction
                        .sessionAttr("user", userSession))  // Simuler la session utilisateur
                .andExpect(status().isOk())  // Vérifier que le statut est 200 OK
                .andExpect(view().name(VIEW_TRANSACTIONS))  // Vérifier que la vue affichée est la bonne
                .andExpect(model().attributeExists("successMessage"));  // Vérifier que le message de succès est bien ajouté au modèle
    }


    @Test
    @DisplayName("Enregistrement d'une transaction - échec de la validation")
    public void testPostTransactionValidationError() throws Exception {
        // Simule l'utilisateur connecté
        UserSessionDTO userSession = new UserSessionDTO(1, "testUser", "test@example.com");

        // Créer un compte simulé avec un solde
        Account account = new Account();
        account.setBalance(100.0);

        // Simule un compte utilisateur avec solde
        when(accountService.getAccountByUserId(userSession.id())).thenReturn(Optional.of(account));

        // Simule des relations utilisateur
        when(relationshipService.getUserRelations(userSession.id())).thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(eq(userSession.id()), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Soumettre une transaction avec des données invalides
        mockMvc.perform(post(URL_TRANSACTIONS)
                        .param("amount", "")
                        .param("description", "Payment for services")
                        .param("receiver.id", "")
                        .sessionAttr("user", userSession))  // Simule la session utilisateur
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_TRANSACTIONS))
                .andExpect(model().attributeHasFieldErrors("Transaction", "amount", "receiver.id"));

        // Vérifie que la méthode de service n'est pas appelée
        verify(transactionService, never()).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));
    }


    @Test
    @DisplayName("Enregistrement d'une transaction - échec de la validation")
    public void testPostTransactionSameUserError() throws Exception {
        // Simule l'utilisateur connecté
        UserSessionDTO userSession = new UserSessionDTO(1, "testUser", "test@example.com");

        // Créer un compte simulé avec un solde
        Account account = new Account();
        account.setBalance(100.0);

        // Simule un compte utilisateur avec solde
        when(accountService.getAccountByUserId(userSession.id())).thenReturn(Optional.of(account));

        // Simule des relations utilisateur
        when(relationshipService.getUserRelations(userSession.id())).thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(eq(userSession.id()), any(Pageable.class)))
                .thenReturn(Page.empty());

        doThrow(new TransactionsException("Vous ne pouvez pas envoyer de l'argent à vous-même."))
                .when(transactionService).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));

        // Soumettre une transaction avec des données invalides
        mockMvc.perform(post(URL_TRANSACTIONS)
                        .param("amount", "50.0")
                        .param("description", "Payment for services")
                        .param("receiver.id", "1")
                        .sessionAttr("user", userSession))  // Simule la session utilisateur
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_TRANSACTIONS))
                .andExpect(model().attributeExists("errorMessage"));
    }

}