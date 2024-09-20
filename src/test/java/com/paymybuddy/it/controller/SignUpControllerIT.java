package com.paymybuddy.it.controller;


import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SignUpControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static final String URL_SIGN_UP = "/sign-up";
    private static final String VIEW_SIGN_UP = "views/sign-up";
    private static final String REDIRECT_URL_SUCCESS = "/sign-in";

    private static final String USERNAME = "Bidule";
    private static final String EMAIL = "bidule@gmail.com";
    private static final String PASSWORD = "Abc123@!";


    @Test
    @DisplayName("Sign up - page d'inscription")
    public void testGetSignUp() throws Exception {
        mockMvc.perform(get(URL_SIGN_UP))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_SIGN_UP))
                .andExpect(model().attributeExists("title"));
    }


    @Test
    @DisplayName("Sign up - succès de l'inscription")
    public void testSignUpSuccess() throws Exception {

        // Simule l'inscription d'un utilisateur
        doNothing().when(userService).registerUser(any(User.class));

        // Simule la requête POST d'inscription
        mockMvc.perform(post(URL_SIGN_UP)
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .param("passwordConfirmation", PASSWORD))
                .andExpect(status().is3xxRedirection()) // vérifie que le statut de la réponse est une redirection
                .andExpect(redirectedUrl(REDIRECT_URL_SUCCESS)) // vérifie que la redirection est bien effectuée
                .andExpect(flash().attributeExists("successMessage")); // vérifie que le message de succès est bien présent

        // Vérifie que la méthode registerUser a bien été appelée
        verify(userService).registerUser(any(User.class));
    }

    @Test
    @DisplayName("Sign up - champs vides")
    public void testSignUpEmptyFields() throws Exception {
        // Simule la requête POST d'inscription avec des champs vides
        mockMvc.perform(post(URL_SIGN_UP)
                        .param("username", "")
                        .param("email", "")
                        .param("password", "")
                        .param("passwordConfirmation", ""))
                .andExpect(status().isOk()) // vérifie que le statut de la réponse est OK
                .andExpect(view().name(VIEW_SIGN_UP)) // vérifie que la vue est bien celle attendue
                .andExpect(model().attributeHasFieldErrors("user", "username", "email", "password")); // vérifie que les champs sont bien en erreur

        // Vérifie que la méthode registerUser n'est pas appelée
        verify(userService, never()).registerUser(any(User.class));
    }


    @Test
    @DisplayName("Sign up - mots de passe non identique")
    public void testSignUpDifferentPasswords() throws Exception {

        // Simule la vérification des mots de passe
        doAnswer(invocation -> {
            BindingResult result = invocation.getArgument(1);

            // Ajoute les erreurs pour les champs password et passwordConfirmation
            result.rejectValue("password", "error.user", "Les mots de passe ne correspondent pas.");
            result.rejectValue("passwordConfirmation", "error.user", "Les mots de passe ne correspondent pas.");

            return null;
        }).when(userService).userVerification(any(User.class), any(BindingResult.class));


        // Simule la requête POST d'inscription avec des mots de passe différents
        mockMvc.perform(post(URL_SIGN_UP)
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .param("passwordConfirmation", "DifferentPassword"))
                .andExpect(status().isOk()) // vérifie que le statut de la réponse est OK
                .andExpect(view().name(VIEW_SIGN_UP)) // vérifie que la vue est bien celle attendue
                .andExpect(model().attributeHasFieldErrors("user", "password", "passwordConfirmation")); // vérifie que le champ password et passwordConfirmation sont en erreur

        // Vérifie que la méthode registerUser n'est pas appelée
        verify(userService, never()).registerUser(any(User.class));
    }


    @Test
    @DisplayName("Sign up - email déjà utilisé")
    public void testSignUpEmailAlreadyUsed() throws Exception {

        // Simule l'existence d'un utilisateur avec la même adresse mail
        doAnswer(invocation -> {
            BindingResult result = invocation.getArgument(1);

            // Ajoute l'erreur pour le champ email
            result.rejectValue("email", "error.user", "Cette adresse mail est déjà utilisée.");

            return null;
        }).when(userService).userVerification(any(User.class), any(BindingResult.class));

        // Simule la requête POST d'inscription
        mockMvc.perform(post(URL_SIGN_UP)
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .param("passwordConfirmation", PASSWORD))
                .andExpect(status().isOk()) // vérifie que le statut de la réponse est OK
                .andExpect(view().name(VIEW_SIGN_UP)) // vérifie que la vue est bien celle attendue
                .andExpect(model().attributeHasFieldErrors("user", "email")); // vérifie que le message d'erreur est bien présent

        // Vérifie que la méthode registerUser n'est pas appelée
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    @DisplayName("Sign up - username déjà utilisé")
    public void testSignUpUsernameAlreadyUsed() throws Exception {

        // Simule l'existence d'un utilisateur avec le même nom d'utilisateur
        doAnswer(invocation -> {
            BindingResult result = invocation.getArgument(1);

            // Ajoute l'erreur pour le champ username
            result.rejectValue("username", "error.user", "Ce nom d'utilisateur existe déjà.");

            return null;
        }).when(userService).userVerification(any(User.class), any(BindingResult.class));

        // Simule la requête POST d'inscription
        mockMvc.perform(post(URL_SIGN_UP)
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .param("passwordConfirmation", PASSWORD))
                .andExpect(status().isOk()) // vérifie que le statut de la réponse est OK
                .andExpect(view().name(VIEW_SIGN_UP)) // vérifie que la vue est bien celle attendue
                .andExpect(model().attributeHasFieldErrors("user", "username")); // vérifie que le message d'erreur est bien présent

        // Vérifie que la méthode registerUser n'est pas appelée
        verify(userService, never()).registerUser(any(User.class));
    }
}
