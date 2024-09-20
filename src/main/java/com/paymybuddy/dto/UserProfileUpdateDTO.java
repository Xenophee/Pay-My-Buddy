package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserProfileUpdateDTO implements UserProfile {
        @NotEmpty(message = "Veuillez saisir un nom d'utilisateur.")
        @Size(min = 4, max = 50, message = "Veuillez saisir un nom d'utilisateur entre 4 et 50 caractères.")
        private String username;

        @NotEmpty(message = "Veuillez saisir un email.")
        @Email(message = "Veuillez saisir un email valide.")
        private String email;

        @Pattern(
                regexp = "^(|(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!?%^&+=]).{8,})$",
                message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial (@#$!?%^&+=)"
        )
        private String password;

        private String passwordConfirmation;
}
