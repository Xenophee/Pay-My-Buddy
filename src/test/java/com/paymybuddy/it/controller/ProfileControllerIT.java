package com.paymybuddy.it.controller;


import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static final String URL_PROFILE = "/profil";
    private static final String VIEW_PROFILE = "views/profile";
    private static final String REDIRECT_URL_NO_SESSION = "/sign-in";


    @Test
    @DisplayName("GET /profil - succès")
    public void testGetProfile() throws Exception {
        // Simule la session utilisateur
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        mockMvc.perform(get(URL_PROFILE).sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeExists("title", "user"));
    }

    @Test
    @DisplayName("GET /profil - échec en l'absence de session utilisateur")
    public void testGetProfileNoSession() throws Exception {
        // Simule une requête sans session utilisateur
        mockMvc.perform(get(URL_PROFILE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_URL_NO_SESSION));
    }

    @Test
    @DisplayName("POST /profil - succès sans modification de mot de passe")
    public void testPostProfileWithoutPasswordSuccess() throws Exception {
        // Simule la session utilisateur
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        when(userService.updateUser(any(UserProfileUpdateDTO.class), eq(userSession.id()))).thenReturn(mock(User.class));

        // Exécute la requête POST /profil avec des données valides sans modification de mot de passe
        mockMvc.perform(post(URL_PROFILE)
                        .sessionAttr("user", userSession)
                        .param("username", "Jean2")
                        .param("email", "jean@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeExists("successMessage"));

        verify(userService).userVerification(any(UserProfileUpdateDTO.class), any(BindingResult.class), eq(userSession));
        verify(userService).updateUser(any(UserProfileUpdateDTO.class), eq(userSession.id()));
    }

    @Test
    @DisplayName("POST /profil - succès")
    public void testPostProfileSuccess() throws Exception {
        // Simule la session utilisateur
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        when(userService.updateUser(any(UserProfileUpdateDTO.class), eq(userSession.id()))).thenReturn(mock(User.class));

        // Exécute la requête POST /profil avec des données valides
        mockMvc.perform(post(URL_PROFILE)
                        .sessionAttr("user", userSession)
                        .param("username", "Jean")
                        .param("email", "jean@gmail.com")
                        .param("password", "Abc123@!!")
                        .param("passwordConfirmation", "Abc123@!!"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeExists("successMessage"));

        verify(userService).userVerification(any(UserProfileUpdateDTO.class), any(BindingResult.class), eq(userSession));
        verify(userService).updateUser(any(UserProfileUpdateDTO.class), eq(userSession.id()));
    }


    @Test
    @DisplayName("POST /profil - échec (erreurs de validation)")
    public void testUpdateUserProfileFailure() throws Exception {
        // Simule la session utilisateur
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        // Exécute la requête POST /profil avec des données invalides
        mockMvc.perform(post(URL_PROFILE)
                        .sessionAttr("user", userSession)
                        .param("username", "")
                        .param("email", "")
                        .param("password", "Abc")
                        .param("passwordConfirmation", "Abc"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeHasFieldErrors("user", "username", "email", "password"));

        verify(userService).userVerification(any(UserProfileUpdateDTO.class), any(BindingResult.class), eq(userSession));
        verify(userService, never()).updateUser(any(UserProfileUpdateDTO.class), eq(userSession.id()));
    }
}
