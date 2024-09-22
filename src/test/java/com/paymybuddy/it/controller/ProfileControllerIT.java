package com.paymybuddy.it.controller;


import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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


@DisplayName("EndPoint - /profil")
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

    private UserSessionDTO userSession;

    @BeforeEach
    public void setUp() {
        // Initialisation de l'objet UserSessionDTO
        userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");
    }


    @Test
    @DisplayName("GET /profil - succès")
    public void testGetProfile() throws Exception {
        mockMvc.perform(get(URL_PROFILE)
                        .sessionAttr("user", userSession))
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

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /profil - succès sans modification de mot de passe")
    public void testPostProfileWithoutPasswordSuccess() throws Exception {

        when(userService.updateUser(any(UserProfileUpdateDTO.class), anyLong()))
                .thenReturn(mock(User.class));

        // Exécute la requête POST /profil avec des données valides sans modification de mot de passe
        mockMvc.perform(post(URL_PROFILE)
                        .sessionAttr("user", userSession)
                        .param("username", "Jean2")
                        .param("email", "jean2@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeExists("successMessage"));

        verify(userService).userVerification(any(UserProfileUpdateDTO.class), any(BindingResult.class), any(UserSessionDTO.class));
        verify(userService).updateUser(any(UserProfileUpdateDTO.class), anyLong());
    }

    @Test
    @DisplayName("POST /profil - succès")
    public void testPostProfileSuccess() throws Exception {

        String newPassword = "Abc123@!!";

        when(userService.updateUser(any(UserProfileUpdateDTO.class), anyLong()))
                .thenReturn(mock(User.class));

        // Exécute la requête POST /profil avec des données valides
        mockMvc.perform(post(URL_PROFILE)
                        .sessionAttr("user", userSession)
                        .param("username", userSession.username())
                        .param("email", userSession.email())
                        .param("password", newPassword)
                        .param("passwordConfirmation", newPassword))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeExists("successMessage"));

        verify(userService).userVerification(any(UserProfileUpdateDTO.class), any(BindingResult.class), any(UserSessionDTO.class));
        verify(userService).updateUser(any(UserProfileUpdateDTO.class), anyLong());
    }


    @Test
    @DisplayName("POST /profil - échec (erreurs de validation)")
    public void testUpdateUserProfileFailure() throws Exception {

        String badPassword = "Abc";

        // Exécute la requête POST /profil avec des données invalides
        mockMvc.perform(post(URL_PROFILE)
                        .sessionAttr("user", userSession)
                        .param("username", "")
                        .param("email", "")
                        .param("password", badPassword)
                        .param("passwordConfirmation", badPassword))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_PROFILE))
                .andExpect(model().attributeHasFieldErrors("user", "username", "email", "password"));

        verify(userService).userVerification(any(UserProfileUpdateDTO.class), any(BindingResult.class), any(UserSessionDTO.class));
        verify(userService, never()).updateUser(any(UserProfileUpdateDTO.class), anyLong());
    }
}
