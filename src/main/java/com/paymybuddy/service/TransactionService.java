package com.paymybuddy.service;


import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.TransactionsException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.RelationshipRepository;
import com.paymybuddy.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service class for managing transactions.
 */
@Service
public class TransactionService {

    public final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final RelationshipRepository relationshipRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService, RelationshipRepository relationshipRepository) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.relationshipRepository = relationshipRepository;
    }

    /**
     * Registers a new transaction.
     *
     * @param user the user session data
     * @param transaction the transaction to register
     * @throws TransactionsException if the transaction is invalid
     */
    public void registerTransaction(UserSessionDTO user, Transaction transaction) {

        Account account = accountService.getAccountByUserId(user.id()).get();

        if (user.id() == transaction.getReceiver().getId()) {
            logger.warn("L'utilisateur ne peut pas s'envoyer de l'argent à lui-même : {}", user);
            throw new TransactionsException("Vous ne pouvez pas envoyer de l'argent à vous-même.");
        }

        if (!relationshipRepository.existsByUserIdAndReceiverId(user.id(), transaction.getReceiver().getId())) {
            logger.warn("L'utilisateur {} n'est pas en relation avec le destinataire {} de la transaction", user.username(), transaction.getReceiver().getUsername());
            throw new TransactionsException("Vous n'êtes pas en relation avec le destinataire de la transaction.");
        }

        if (account.getBalance() < transaction.getAmount()) {
            logger.warn("Le solde du compte est insuffisant ( {} ) pour effectuer la transaction d'un montant de {}.", account.getBalance(), transaction.getAmount());
            throw new TransactionsException("Le solde du compte est insuffisant pour effectuer la transaction.");
        }

        User sender = new User();
        sender.setId(user.id());
        transaction.setSender(sender);

        transactionRepository.save(transaction);
    }

    /**
     * Retrieves a paginated list of transactions for a user.
     *
     * @param userId the ID of the user
     * @param pageable the pagination information
     * @return a page of transaction projections
     */
    public Page<TransactionProjection> getTransactions(long userId, Pageable pageable) {
        return transactionRepository.findAllByUserIdWithUsernames(userId, pageable);
    }

}
