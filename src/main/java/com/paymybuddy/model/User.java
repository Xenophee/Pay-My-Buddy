package com.paymybuddy.model;

import com.paymybuddy.dto.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "users")
public class User implements UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    @Size(min = 4, max = 50, message = "Veuillez saisir un nom d''utilisateur entre 4 et 50 caractères.")
    @NotEmpty(message = "Veuillez saisir un nom d''utilisateur.")
    @Setter
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @NotEmpty(message = "Veuillez saisir un email.")
    @Email(message = "Veuillez saisir un email valide.")
    @Setter
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty(message = "Veuillez saisir un mot de passe.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!?%^&+=]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial (@#$!?%^&+=)"
    )
    @Setter
    private String password;


    /**
     * The password confirmation for the user.
     * This field is not persisted in the database.
     */
    @Transient
    @NotEmpty(message = "Veuillez confirmer le mot de passe.")
    @Setter
    String passwordConfirmation;


    /**
     * The timestamp when the user was created.
     * This field is automatically populated by the database.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


    /**
     * The timestamp when the user was last updated.
     * This field is automatically populated by the database.
     */
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
