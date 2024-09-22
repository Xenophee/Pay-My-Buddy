package com.paymybuddy.it.controller;


import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.TransactionsException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.RelationshipService;
import com.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("EndPoint - /transactions")
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

    private UserSessionDTO userSession;

    @BeforeEach
    public void setUp() {
        // Initialisation de l'objet UserSessionDTO
        userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");
    }


    @Test
    @DisplayName("GET /transactions - succès")
    public void testGetTransactions() throws Exception {

        // Simule la récupération du compte associé à l'utilisateur
        when(accountService.getAccountByUserId(anyLong()))
                .thenReturn(Optional.of(mock(Account.class)));

        // Simule la récupération des relations utilisateur
        when(relationshipService.getUserRelations(anyLong()))
                .thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Exécute la requête GET /transactions avec une session utilisateur
        mockMvc.perform(get(URL_TRANSACTIONS)
                        .sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_TRANSACTIONS))
                .andExpect(model().attributeExists("title", "js", "transactionsPage", "relationships", "Transaction"));

        // Vérifie que les méthodes du service ont été appelées correctement
        verify(accountService).getAccountByUserId(anyLong());
        verify(relationshipService).getUserRelations(anyLong());
        verify(transactionService).getTransactions(anyLong(), any(Pageable.class));
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
    @DisplayName("POST /transactions - succès")
    public void testPostTransactionSuccess() throws Exception {

        // Simuler un compte utilisateur avec solde
        when(accountService.getAccountByUserId(anyLong()))
                .thenReturn(Optional.of(mock(Account.class)));

        // Simuler des relations utilisateur
        when(relationshipService.getUserRelations(anyLong()))
                .thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Simuler une transaction réussie
        doNothing().when(transactionService).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));

        // Soumettre une nouvelle transaction avec des données valides
        mockMvc.perform(post(URL_TRANSACTIONS)
                        .param("amount", "50.0")
                        .param("description", "Payment for services")
                        .param("receiver.id", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_TRANSACTIONS))
                .andExpect(model().attributeExists("successMessage"));

        verify(accountService).getAccountByUserId(anyLong());
        verify(relationshipService).getUserRelations(anyLong());
        verify(transactionService).getTransactions(anyLong(), any(Pageable.class));
        verify(transactionService).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));
    }


    @Test
    @DisplayName("POST /transactions - échec (champs vides)")
    public void testPostTransactionValidationError() throws Exception {

        // Simule un compte utilisateur avec solde
        when(accountService.getAccountByUserId(anyLong()))
                .thenReturn(Optional.of(mock(Account.class)));

        // Simule des relations utilisateur
        when(relationshipService.getUserRelations(anyLong()))
                .thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(anyLong(), any(Pageable.class)))
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

        verify(accountService).getAccountByUserId(anyLong());
        verify(relationshipService).getUserRelations(anyLong());
        verify(transactionService).getTransactions(anyLong(), any(Pageable.class));
        verify(transactionService, never()).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));
    }


    @Test
    @DisplayName("POST /transactions - échec (même utilisateur)")
    public void testPostTransactionSameUserError() throws Exception {

        // Simule un compte utilisateur avec solde
        when(accountService.getAccountByUserId(anyLong()))
                .thenReturn(Optional.of(mock(Account.class)));

        // Simule des relations utilisateur
        when(relationshipService.getUserRelations(anyLong()))
                .thenReturn(List.of());

        // Simule la récupération des transactions paginées
        when(transactionService.getTransactions(anyLong(), any(Pageable.class)))
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

        verify(accountService).getAccountByUserId(anyLong());
        verify(relationshipService).getUserRelations(anyLong());
        verify(transactionService).getTransactions(anyLong(), any(Pageable.class));
        verify(transactionService).registerTransaction(any(UserSessionDTO.class), any(Transaction.class));
    }

}
