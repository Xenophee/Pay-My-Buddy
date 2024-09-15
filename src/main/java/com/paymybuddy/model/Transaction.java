package com.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;



@Getter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @Setter
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @NotNull(message = "Veuillez sélectionner le destinataire.")
    @Setter
    private User receiver;

    @Column(name = "amount", nullable = false)
    @Min(value = 1, message = "Le montant minimum est de 1 €.")
    @Setter
    private double amount;

    @Column(name = "description")
    @Size(max = 250, message = "La description ne doit pas dépasser 250 caractères.")
    @Setter
    private String description;

    @Column(name = "processed_at", insertable = false, updatable = false)
    private LocalDateTime processedAt;

}
