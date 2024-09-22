package com.paymybuddy.it.service;

import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.AddRelationshipsException;
import com.paymybuddy.exception.RelationshipsException;
import com.paymybuddy.model.Relationship;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.RelationshipRepository;
import com.paymybuddy.service.RelationshipService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("RelationshipService - Tests d'intégration")
public class RelationshipServiceIT {

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private RelationshipRepository relationshipRepository;

    private UserSessionDTO userSession;
    private User otherUser;

    @BeforeEach
    public void setUp() {
        userSession = new UserSessionDTO(1, "Jean", "jean@gmail.com");
        otherUser = new User();
        otherUser.setId(3);
    }

    @Test
    @DisplayName("Ajout d'une relation - succès")
    public void testAddRelationshipSuccess() {
        relationshipService.addRelationship(userSession, otherUser);

        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(otherUser.getId(), userSession.id()).orElse(null);
        assertThat(relationship).isNotNull();
        assertThat(relationship.getId().getRequester().getId()).isEqualTo(userSession.id());
        assertThat(relationship.getId().getReceiver().getId()).isEqualTo(otherUser.getId());
        assertThat(relationship.getValidatedAt()).isNull();
    }

    @Test
    @DisplayName("Ajout d'une relation - échec (même utilisateur)")
    public void testAddRelationshipFailureSameUser() {
        otherUser.setId(userSession.id());
        assertThrows(AddRelationshipsException.class, () -> relationshipService.addRelationship(userSession, otherUser));
    }

    @Test
    @DisplayName("Ajout d'une relation - échec (déjà en attente)")
    public void testAddRelationshipFailureWaitingRelationshipAlreadyExists() {
        otherUser.setId(6);
        assertThrows(AddRelationshipsException.class, () -> relationshipService.addRelationship(userSession, otherUser));
    }

    @Test
    @DisplayName("Ajout d'une relation - échec (déjà en relation)")
    public void testAddRelationshipFailureRelationshipAlreadyExists() {
        otherUser.setId(2);
        assertThrows(AddRelationshipsException.class, () -> relationshipService.addRelationship(userSession, otherUser));
    }

    @Test
    @DisplayName("Valide une relation - succès")
    public void testValidateRelationshipSuccess() {
        otherUser.setId(6);
        relationshipService.validateRelationship(otherUser.getId(), userSession.id());

        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(otherUser.getId(), userSession.id()).orElse(null);
        assertThat(relationship).isNotNull();
        assertThat(relationship.getValidatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Valide une relation - échec (relation inexistante)")
    public void testValidateRelationshipFailure() {
        otherUser.setId(4);
        assertThrows(RelationshipsException.class, () -> relationshipService.validateRelationship(otherUser.getId(), userSession.id()));
    }

    @Test
    @DisplayName("Supprime une relation - succès")
    public void testDeleteRelationshipSuccess() {
        otherUser.setId(6);
        relationshipService.deleteRelationship(userSession.id(), otherUser.getId());

        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(otherUser.getId(), userSession.id()).orElse(null);
        assertThat(relationship).isNull();
    }

    @Test
    @DisplayName("Supprime une relation - échec (relation inexistante)")
    public void testDeleteRelationshipFailure() {
        otherUser.setId(4);
        assertThrows(RelationshipsException.class, () -> relationshipService.deleteRelationship(otherUser.getId(), userSession.id()));
    }

    @Test
    @DisplayName("Récupère les relations - succès")
    public void testGetUserRelationsSuccess() {
        List<UserRelationshipProjection> relationships = relationshipService.getUserRelations(userSession.id());
        assertThat(relationships).isNotEmpty();
    }

    @Test
    @DisplayName("Récupère les relations en attente - succès")
    public void testGetWaitingUserRelationsSuccess() {
        List<UserRelationshipProjection> waitingRelationships = relationshipService.getWaitingUserRelations(userSession.id());
        assertThat(waitingRelationships).isNotEmpty();
    }
}
