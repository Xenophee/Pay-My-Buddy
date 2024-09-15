package com.paymybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "balance", nullable = false)
    private double balance;
}
