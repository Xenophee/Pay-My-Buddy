package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserProfileUpdateDTO implements UserProfile {
        @NotEmpty(message = "Veuillez saisir un nom d'utilisateur.")
        @Size(min = 4, max = 50, message = "Veuillez saisir un nom d'utilisateur entre 4 et 50 caractères.")
        private String username;

        @NotEmpty(message = "Veuillez saisir un email.")
        @Email(message = "Veuillez saisir un email valide.")
        private String email;

        private String password;

        private String passwordConfirmation;
}
