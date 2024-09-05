package com.paymybuddy.model;

import com.paymybuddy.dto.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "users")
public class User implements UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    @Size(min = 4, max = 50, message = "Veuillez saisir un nom d''utilisateur entre 4 et 50 caractères.")
    @NotEmpty(message = "Veuillez saisir un nom d''utilisateur.")
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @NotEmpty(message = "Veuillez saisir un email.")
    @Email(message = "Veuillez saisir un email valide.")
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty(message = "Veuillez saisir un mot de passe.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!?%^&+=]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial (@#$!?%^&+=)"
    )
    private String password;

    @Transient
    @NotEmpty(message = "Veuillez confirmer le mot de passe.")
    String passwordConfirmation;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
