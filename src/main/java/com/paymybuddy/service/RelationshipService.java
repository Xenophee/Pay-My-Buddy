package com.paymybuddy.service;


import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.Relationship;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.RelationshipRepository;
import com.paymybuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class RelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipService.class);

    private final RelationshipRepository relationshipRepository;
    private final UserRepository userRepository;

    @Autowired
    public RelationshipService(RelationshipRepository relationshipRepository, UserRepository userRepository) {
        this.relationshipRepository = relationshipRepository;
        this.userRepository = userRepository;
    }

    public List<UserRelationshipProjection> getUserRelations(Long userId) {
        return relationshipRepository.findUserRelations(userId);
    }

    public void addRelationship(UserSessionDTO requester, User receiver) {

        if(requester.id() == receiver.getId()) {
            logger.warn("L'utilisateur {} ne peut pas être ami avec lui-même.", requester.username());
            throw new RuntimeException("Vous ne pouvez pas être ami avec vous-même.");
        }

        if (relationshipRepository.existsByUserIdAndReceiverId(requester.id(), receiver.getId())) {
            logger.warn("La relation existe déjà entre l'utilisateur {} et l'utilisateur {}", requester.username(), receiver.getUsername());
            throw new RuntimeException("La relation existe déjà.");
        }

        if (relationshipRepository.existsWaitingByUserIdAndReceiverId(requester.id(), receiver.getId())) {
            logger.warn("La relation est en attente entre l'utilisateur {} et l'utilisateur {}", requester.username(), receiver.getUsername());
            throw new RuntimeException("La relation est en attente.");
        }

        User user = new User();
        user.setId(requester.id());

        Relationship relationship = new Relationship();
        Relationship.RelationshipId relationshipId = new Relationship.RelationshipId();
        relationshipId.setRequester(user);
        relationshipId.setReceiver(receiver);
        relationship.setId(relationshipId);

        relationshipRepository.save(relationship);
    }

    public void validateRelationship(long requesterId, long receiverId) {
        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                .orElseThrow(() -> new RuntimeException("La relation n'existe pas."));

        relationship.setValidatedAt(LocalDateTime.now());
        relationshipRepository.save(relationship);
    }

    public void deleteRelationship(long requesterId, long receiverId) {
        Relationship relationship = relationshipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                .orElseThrow(() -> new RuntimeException("La relation n'existe pas."));
        relationshipRepository.delete(relationship);
    }

    public List<UserRelationshipProjection> getWaitingUserRelations(long userId) {
        return relationshipRepository.findWaitingUserRelations(userId);
    }
}
