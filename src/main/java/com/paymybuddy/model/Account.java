package com.paymybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
}
