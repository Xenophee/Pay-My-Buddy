package com.paymybuddy.service;


import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.util.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    public final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public void registerTransaction(UserSessionDTO user, Transaction transaction) {

        Optional<Account> account = accountService.getAccountByUserId(user.id());

        // ! REVOIR L'EXCEPTION
        if (account.get().getBalance() < transaction.getAmount()) {
            logger.warn("Le solde du compte est insuffisant pour effectuer la transaction");
            throw new RuntimeException("Le solde du compte est insuffisant pour effectuer la transaction");
        }

        logger.info("Enregistrement de la transaction en cours");
        User sender = new User();
        sender.setId(user.id());
        transaction.setSender(sender);
        transactionRepository.save(transaction);
    }

    public List<TransactionDTO> getTransactionsByUserIdWithUsernames(Long userId) {

        List<TransactionDTO> transactions = transactionRepository.findAllByUserIdWithUsernames(userId);

        return transactions;
    }

}
