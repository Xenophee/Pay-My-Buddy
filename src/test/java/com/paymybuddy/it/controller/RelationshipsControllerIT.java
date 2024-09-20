package com.paymybuddy.it.controller;

import com.paymybuddy.controller.RelationshipsController;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.AddRelationshipsException;
import com.paymybuddy.exception.RelationshipsException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.RelationshipService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class RelationshipsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RelationshipService relationshipService;

    private static final String URL_RELATIONS = "/relations";
    private static final String VIEW_RELATIONS = "views/relationships";
    private static final String RELATIONS_REDIRECT = "/relations";
    private static final String REDIRECT_URL_NO_SESSION = "/sign-in";
    @Autowired
    private RelationshipsController relationshipsController;


    @Test
    @DisplayName("GET /relations - succès")
    public void testGetRelationships() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        when(relationshipService.getUserRelations(userSession.id())).thenReturn(List.of());
        when(relationshipService.getWaitingUserRelations(userSession.id())).thenReturn(List.of());

        mockMvc.perform(get(URL_RELATIONS)
                        .sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_RELATIONS))
                .andExpect(model().attributeExists("title", "userRelations", "waitingUserRelations"));

        verify(relationshipService).getUserRelations(userSession.id());
        verify(relationshipService).getWaitingUserRelations(userSession.id());
    }

    @Test
    @DisplayName("GET /relations - échec en l'absence de session utilisateur")
    public void testGetRelationshipsNoSession() throws Exception {
        // Simule une requête sans session utilisateur
        mockMvc.perform(get(URL_RELATIONS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_URL_NO_SESSION));
    }


    @Test
    @DisplayName("POST /relations - succès")
    public void testAddRelationship() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(mock(User.class)));
        doNothing().when(relationshipService).addRelationship(any(UserSessionDTO.class), any(User.class));

        mockMvc.perform(post(URL_RELATIONS)
                        .param("email", "receiver@gmail.com")
                        .sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_RELATIONS))
                .andExpect(model().attributeExists("successMessage", "title", "userRelations", "waitingUserRelations"));

        verify(relationshipService).addRelationship(eq(userSession), any(User.class));
    }

    @Test
    @DisplayName("POST /relations - échec (utilisateur non trouvé)")
    public void testAddRelationshipUserNotFound() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post(URL_RELATIONS)
                        .param("email", "unknown@gmail.com")
                        .sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_RELATIONS))
                .andExpect(model().attributeExists("errorMessage", "title", "userRelations", "waitingUserRelations"));

        verify(relationshipService, never()).addRelationship(any(UserSessionDTO.class), any(User.class));
    }

    @Test
    @DisplayName("POST /relations - échec (même utilisateur)")
    public void testAddRelationshipSameUserAsSession() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(mock(User.class)));
        doThrow(new AddRelationshipsException("Vous ne pouvez pas être ami avec vous-même.")).when(relationshipService).addRelationship(any(UserSessionDTO.class), any(User.class));

        mockMvc.perform(post(URL_RELATIONS)
                        .param("email", "jean@gmail.com")
                        .sessionAttr("user", userSession))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_RELATIONS))
                .andExpect(model().attributeExists("errorMessage", "userRelations", "waitingUserRelations"));

        verify(relationshipService).addRelationship(eq(userSession), any(User.class));
    }

    @Test
    @DisplayName("PUT /relations/accept - succès")
    public void testAcceptWaitingRelationship() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        doNothing().when(relationshipService).validateRelationship(2, userSession.id());

        mockMvc.perform(put(URL_RELATIONS + "/accept")
                        .param("requesterId", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RELATIONS_REDIRECT))
                .andExpect(flash().attributeExists("successMessage"));

        verify(relationshipService).validateRelationship(2, userSession.id());
    }

    @Test
    @DisplayName("PUT /relations/accept - échec (relation inexistante)")
    public void testAcceptWaitingRelationshipFailure() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        doThrow(new RelationshipsException("La relation n'existe pas.")).when(relationshipService).validateRelationship(2, userSession.id());

        mockMvc.perform(put(URL_RELATIONS + "/accept")
                        .param("requesterId", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RELATIONS_REDIRECT))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(relationshipService).validateRelationship(2, userSession.id());
    }

    @Test
    @DisplayName("DELETE /relations/delete - succès")
    public void testDeleteWaitingRelationship() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        doNothing().when(relationshipService).deleteRelationship(2, userSession.id());

        mockMvc.perform(delete(URL_RELATIONS + "/delete")
                        .param("requesterId", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RELATIONS_REDIRECT))
                .andExpect(flash().attributeExists("successMessage"));

        verify(relationshipService).deleteRelationship(2, userSession.id());
    }

    @Test
    @DisplayName("DELETE /relations/delete - échec (relation inexistante)")
    public void testDeleteWaitingRelationshipFailure() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        doThrow(new RelationshipsException("La relation n'existe pas.")).when(relationshipService).deleteRelationship(2, userSession.id());

        mockMvc.perform(delete(URL_RELATIONS + "/delete")
                        .param("requesterId", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RELATIONS_REDIRECT))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(relationshipService).deleteRelationship(2, userSession.id());
    }

    @Test
    @DisplayName("DELETE /relations - succès")
    public void testDeleteRelationship() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        doNothing().when(relationshipService).deleteRelationship(userSession.id(), 2);

        mockMvc.perform(delete(URL_RELATIONS)
                        .param("deleteRelationship", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RELATIONS_REDIRECT))
                .andExpect(flash().attributeExists("successMessage"));

        verify(relationshipService).deleteRelationship(userSession.id(), 2);
    }


    @Test
    @DisplayName("DELETE /relations - échec (relation inexistante)")
    public void testDeleteRelationshipFailure() throws Exception {
        UserSessionDTO userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");

        doThrow(new RelationshipsException("La relation n'existe pas.")).when(relationshipService).deleteRelationship(userSession.id(), 2);

        mockMvc.perform(delete(URL_RELATIONS)
                        .param("deleteRelationship", "2")
                        .sessionAttr("user", userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RELATIONS_REDIRECT))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(relationshipService).deleteRelationship(userSession.id(), 2);
    }
}
