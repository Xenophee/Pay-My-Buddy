package com.paymybuddy.service;


import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.TransactionsException;
import com.paymybuddy.exception.UnexpectedNotFoundException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TransactionService {

    public final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public void registerTransaction(UserSessionDTO user, Transaction transaction) {

        Account account = accountService.getAccountByUserId(user.id())
                .orElseThrow(() -> {
                    logger.error("Aucun compte trouvé pour l'utilisateur : {}", user);
                    return new UnexpectedNotFoundException("Le compte n'existe pas.");
                });

        if (account.getBalance() < transaction.getAmount()) {
            logger.warn("Le solde du compte est insuffisant pour effectuer la transaction");
            throw new TransactionsException("Le solde du compte est insuffisant pour effectuer la transaction");
        }

        User sender = new User();
        sender.setId(user.id());
        transaction.setSender(sender);

        transactionRepository.save(transaction);
    }

    public List<TransactionProjection> getTransactionsByUserIdWithUsernames(long userId) {
        return transactionRepository.findAllByUserIdWithUsernames(userId);
    }

}
