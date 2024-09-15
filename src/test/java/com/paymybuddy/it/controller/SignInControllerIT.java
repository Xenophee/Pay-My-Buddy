package com.paymybuddy.it.controller;


import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SignInException;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignInControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

//    @MockBean
//    private UserRepository userRepository;


    private static final long ID = 1;
    private static final String USERNAME = "Jean";
    private static final String EMAIL = "jean@gmail.com";
    private static final String PASSWORD = "$2a$10$QWDIWUBgiITPXVTEJZ8goeZGmgVuEM6pyhsia1zZ2BLeyznBbNWly";

    private static final String URL_SIGN_IN = "/sign-in";
    private static final String VIEW_SIGN_IN = "views/sign-in";
    private static final String REDIRECT_URL_SUCCESS = "/transactions";


    @Test
    @DisplayName("Sign in - page de connexion")
    public void testGetSignIn() throws Exception {
        mockMvc.perform(get(URL_SIGN_IN))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_SIGN_IN))
                .andExpect(model().attributeExists("title"));
    }


    @Test
    @DisplayName("Sign in - succès de l'authentification")
    public void testSignInSuccess() throws Exception {
        // Simule un utilisateur dans la base de données
        UserSessionDTO userSessionDTO = new UserSessionDTO(ID, USERNAME, EMAIL);

        // Simule une authentification réussie
        when(userService.authenticateUser(EMAIL, PASSWORD))
                .thenReturn(userSessionDTO);

        // Simule la requête POST de connexion
        mockMvc.perform(post(URL_SIGN_IN)
                        .param("email", EMAIL)
                        .param("password", PASSWORD))
                .andExpect(status().is3xxRedirection()) // Redirection après succès
                .andExpect(redirectedUrl(REDIRECT_URL_SUCCESS)) // Redirection vers la page attendue
                .andExpect(request().sessionAttribute("user", userSessionDTO)); // Vérifier que la session utilisateur est créée

        // Vérifie que le service d'authentification a bien été appelé
        verify(userService).authenticateUser(EMAIL, PASSWORD);
    }


    @Test
    @DisplayName("Sign in - échec de l'authentification")
    public void testSignInFailure() throws Exception {
        // Simule un échec d'authentification
        when(userService.authenticateUser("wrong@example.com", "wrongpassword"))
                .thenThrow(new SignInException("Invalid credentials"));

        // Simule la requête POST de connexion avec des identifiants incorrects
        mockMvc.perform(post(URL_SIGN_IN)
                        .param("email", "wrong@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk()) // La page de connexion est réaffichée
                .andExpect(view().name(VIEW_SIGN_IN)) // Renvoie de la vue de connexion
                .andExpect(model().attributeExists("errorMessage")) // Un message d'erreur est affiché
                .andExpect(request().sessionAttributeDoesNotExist("user")); // Vérifie que la session utilisateur n'est pas créée

        // Vérifie que le service d'authentification a bien été appelé
        verify(userService).authenticateUser("wrong@example.com", "wrongpassword");
    }

    @Test
    @DisplayName("Sign in - champs vides")
    public void testSignInEmptyFields() throws Exception {

        // Simule la requête POST de connexion avec des identifiants non renseignés
        mockMvc.perform(post(URL_SIGN_IN)
                        .param("email", "")
                        .param("password", ""))
                .andExpect(status().isOk()) // La page de connexion est réaffichée
                .andExpect(view().name(VIEW_SIGN_IN)) // Renvoie de la vue de connexion
                .andExpect(model().attributeExists("errorMessage")) // Un message d'erreur est affiché
                .andExpect(request().sessionAttributeDoesNotExist("user")); // Vérifie que la session utilisateur n'est pas créée

        // Vérifie que le service d'authentification n'a pas été appelé
        verify(userService, never()).authenticateUser(anyString(), anyString());
    }
}
