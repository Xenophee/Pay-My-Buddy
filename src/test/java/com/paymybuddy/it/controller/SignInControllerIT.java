package com.paymybuddy.it.controller;


import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SignInException;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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


@DisplayName("EndPoint - /sign-in")
@SpringBootTest
@AutoConfigureMockMvc
public class SignInControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    private static final long ID = 1;
    private static final String USERNAME = "Jean";
    private static final String EMAIL = "jean@gmail.com";
    private static final String PASSWORD = "Abc123@!";

    private static final String URL_SIGN_IN = "/sign-in";
    private static final String VIEW_SIGN_IN = "views/sign-in";
    private static final String REDIRECT_URL_SUCCESS = "/transactions";

    private UserSessionDTO userSession;

    @BeforeEach
    public void setUp() {
        // Initialisation de l'objet UserSessionDTO
        userSession = new UserSessionDTO(ID, USERNAME, EMAIL);
    }


    @Test
    @DisplayName("GET /sign-in - succès")
    public void testGetSignIn() throws Exception {
        mockMvc.perform(get(URL_SIGN_IN))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_SIGN_IN))
                .andExpect(model().attributeExists("title"));
    }


    @Test
    @DisplayName("POST /sign-in - succès de l'authentification")
    public void testSignInSuccess() throws Exception {

        // Simule une authentification réussie
        when(userService.authenticateUser(anyString(), anyString()))
                .thenReturn(userSession);

        // Simule la requête POST de connexion
        mockMvc.perform(post(URL_SIGN_IN)
                        .param("email", EMAIL)
                        .param("password", PASSWORD))
                .andExpect(status().is3xxRedirection()) // Redirection après succès
                .andExpect(redirectedUrl(REDIRECT_URL_SUCCESS)) // Redirection vers la page attendue
                .andExpect(request().sessionAttribute("user", userSession)); // Vérifier que la session utilisateur est créée

        // Vérifie que le service d'authentification a bien été appelé
        verify(userService).authenticateUser(anyString(), anyString());
    }


    @Test
    @DisplayName("POST /sign-in - échec (identifiants incorrects)")
    public void testSignInFailure() throws Exception {
        // Simule un échec d'authentification
        when(userService.authenticateUser(anyString(), anyString()))
                .thenThrow(new SignInException("Identifiants incorrects"));

        // Simule la requête POST de connexion avec des identifiants incorrects
        mockMvc.perform(post(URL_SIGN_IN)
                        .param("email", "wrong@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk()) // La page de connexion est réaffichée
                .andExpect(view().name(VIEW_SIGN_IN)) // Renvoie de la vue de connexion
                .andExpect(model().attributeExists("errorMessage")) // Un message d'erreur est affiché
                .andExpect(request().sessionAttributeDoesNotExist("user")); // Vérifie que la session utilisateur n'est pas créée

        // Vérifie que le service d'authentification a bien été appelé
        verify(userService).authenticateUser(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /sign-in - échec (champs vides)")
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
