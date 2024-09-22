package com.paymybuddy.service;


import com.paymybuddy.model.Account;
import com.paymybuddy.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Service class for managing accounts.
 */
@Service
public class AccountService {

    public final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves an account by the user ID.
     *
     * @param id the ID of the user
     * @return an Optional containing the account if found, or empty if not
     */
    public Optional<Account> getAccountByUserId(long id) {
        return accountRepository.findById(id);
    }
}
