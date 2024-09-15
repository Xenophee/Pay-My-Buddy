package com.paymybuddy.it.service;


import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.TransactionsException;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;



@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TransactionService - Tests d'intégration")
public class TransactionServiceIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;


    private UserSessionDTO userSession;
    private User receiver;
    private Transaction transaction;


    @BeforeEach
    public void setUp() {
        userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        receiver = new User();
        receiver.setId(2);

        transaction = new Transaction();
        transaction.setDescription("Test");
        transaction.setAmount(50.0);
        transaction.setReceiver(receiver);
    }


    @Test
    @DisplayName("Ajout d'une transaction - succès")
    public void testRegisterTransactionSuccess() {

        // Appelle le service pour enregistrer la transaction
        transactionService.registerTransaction(userSession, transaction);

        // Vérifie que la transaction a bien été enregistrée
        Transaction savedTransaction = transactionRepository.findById(transaction.getId()).orElse(null);
        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(savedTransaction.getSender().getId()).isEqualTo(userSession.id());
        assertThat(savedTransaction.getReceiver().getId()).isEqualTo(receiver.getId());
        assertThat(savedTransaction.getDescription()).isNotNull();
    }

    @Test
    @DisplayName("Ajout d'une transaction - échec (destinataire identique à l'expéditeur)")
    public void registerTransactionSelfTransaction() {
        transaction.getReceiver().setId(userSession.id());
        assertThrows(TransactionsException.class, () -> transactionService.registerTransaction(userSession, transaction));
    }


    @ParameterizedTest
    @ValueSource(longs = {3, 6})
    @DisplayName("Ajout d'une transaction - échec (destinataire non en relation ou en attente)")
    public void registerTransactionNoRelationship(long receiverId) {
        transaction.getReceiver().setId(receiverId);
        assertThrows(TransactionsException.class, () -> transactionService.registerTransaction(userSession, transaction));
    }


    @Test
    @DisplayName("Ajout d'une transaction - échec (solde insuffisant)")
    public void registerTransactionInsufficientBalance() {
        transaction.setAmount(2000.0);
        assertThrows(TransactionsException.class, () -> transactionService.registerTransaction(userSession, transaction));
    }


    @Test
    @DisplayName("Récupération des transactions")
    public void testGetTransactions() {

        // Simule l'appel à la méthode de récupération des transactions
        Pageable pageable = PageRequest.of(0, 10);
        long userId = 1;

        Page<TransactionProjection> transactions = transactionService.getTransactions(userId, pageable);

        // Vérifie que les transactions sont récupérées
        assertThat(transactions).isNotNull();
        assertThat(transactions.hasContent()).isTrue();
    }
}
