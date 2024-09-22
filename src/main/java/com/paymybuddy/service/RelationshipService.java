package com.paymybuddy.service;


import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.AddRelationshipsException;
import com.paymybuddy.exception.RelationshipsException;
import com.paymybuddy.model.Relationship;
import com.paymybuddy.model.RelationshipId;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.RelationshipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Service class for managing user relationships.
 */
@Service
public class RelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipService.class);

    private final RelationshipRepository relationshipRepository;

    @Autowired
    public RelationshipService(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    /**
     * Retrieves the list of user relationships.
     *
     * @param userId the ID of the user
     * @return a list of user relationship projections
     */
    public List<UserRelationshipProjection> getUserRelations(Long userId) {
        return relationshipRepository.findUserRelations(userId);
    }

    /**
     * Adds a new relationship between the requester and the receiver.
     *
     * @param requester the user session data of the requester
     * @param receiver the user to be added as a friend
     * @throws AddRelationshipsException if the relationship is invalid
     */
    public void addRelationship(UserSessionDTO requester, User receiver) {

        if (requester.id() == receiver.getId()) {
            logger.warn("L'utilisateur {} ne peut pas être ami avec lui-même.", requester.username());
            throw new AddRelationshipsException("Vous ne pouvez pas être ami avec vous-même.");
        }

        if (relationshipRepository.existsWaitingByUserIdAndReceiverId(requester.id(), receiver.getId())) {
            logger.warn("La relation est en attente entre l'utilisateur {} et l'utilisateur {}", requester.username(), receiver.getUsername());
            throw new AddRelationshipsException("La relation est en attente.");
        }

        if (relationshipRepository.existsByUserIdAndReceiverId(requester.id(), receiver.getId())) {
            logger.warn("La relation existe déjà entre l'utilisateur {} et l'utilisateur {}", requester.username(), receiver.getUsername());
            throw new AddRelationshipsException("La relation existe déjà.");
        }

        User user = new User();
        user.setId(requester.id());

        Relationship relationship = new Relationship();
        RelationshipId relationshipId = new RelationshipId();
        relationshipId.setRequester(user);
        relationshipId.setReceiver(receiver);
        relationship.setId(relationshipId);

        relationshipRepository.save(relationship);
    }


    /**
     * Validates an existing relationship.
     *
     * @param requesterId the ID of the requester
     * @param receiverId the ID of the receiver
     * @throws RelationshipsException if the relationship does not exist
     */
    public void validateRelationship(long requesterId, long receiverId) {
        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                .orElseThrow(() -> new RelationshipsException("La relation n'existe pas."));

        relationship.setValidatedAt(LocalDateTime.now());
        relationshipRepository.save(relationship);
    }

    /**
     * Deletes an existing relationship.
     *
     * @param requesterId the ID of the requester
     * @param receiverId the ID of the receiver
     * @throws RelationshipsException if the relationship does not exist
     */
    public void deleteRelationship(long requesterId, long receiverId) {
        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                .orElseThrow(() -> new RelationshipsException("La relation n'existe pas."));
        relationshipRepository.delete(relationship);
    }

    /**
     * Retrieves the list of waiting user relationships.
     *
     * @param userId the ID of the user
     * @return a list of waiting user relationship projections
     */
    public List<UserRelationshipProjection> getWaitingUserRelations(long userId) {
        return relationshipRepository.findWaitingUserRelations(userId);
    }
}
