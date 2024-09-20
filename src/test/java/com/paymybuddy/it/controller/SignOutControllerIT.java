package com.paymybuddy.it.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SignOutControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Sign out - succès de la déconnexion")
    public void testSignOut() throws Exception {
        // Simule une session utilisateur à déconnecter
        HttpSession session = mockMvc.perform(get("/sign-out")
                        .sessionAttr("user", "testUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn()
                .getRequest()
                .getSession(false);

        // Vérifie que la session n'existe plus
        assert session == null;
    }
}