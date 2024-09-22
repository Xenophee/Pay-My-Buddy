package com.paymybuddy.repository;

import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository interface for managing Relationship entities.
 */
@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {


    /**
     * Checks if a validated relationship exists between two users.
     *
     * @param userId the ID of the first user
     * @param receiverId the ID of the second user
     * @return true if a validated relationship exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Relationship r " +
            "WHERE ((r.id.requester.id = :receiverId AND r.id.receiver.id = :userId) OR (r.id.requester.id = :userId AND r.id.receiver.id = :receiverId)) AND r.validatedAt IS NOT NULL")
    boolean existsByUserIdAndReceiverId(@Param("userId") long userId, @Param("receiverId") long receiverId);


    /**
     * Checks if a pending relationship exists between two users.
     *
     * @param userId the ID of the first user
     * @param receiverId the ID of the second user
     * @return true if a pending relationship exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Relationship r " +
            "WHERE ((r.id.requester.id = :receiverId AND r.id.receiver.id = :userId) OR (r.id.requester.id = :userId AND r.id.receiver.id = :receiverId)) AND r.validatedAt IS NULL")
    boolean existsWaitingByUserIdAndReceiverId(long userId, long receiverId);


    /**
     * Finds all validated relationships for a user.
     *
     * @param userId the ID of the user
     * @return a list of user relationship projections
     */
    @Query("SELECT u.id as id, u.username as username FROM User u " +
            "JOIN Relationship r ON (r.id.requester = u OR r.id.receiver = u) " +
            "WHERE (r.id.requester.id = :userId OR r.id.receiver.id = :userId) AND u.id <> :userId AND r.validatedAt IS NOT NULL")
    List<UserRelationshipProjection> findUserRelations(@Param("userId") long userId);


    /**
     * Finds all pending relationships for a user.
     *
     * @param userId the ID of the user
     * @return a list of user relationship projections
     */
    @Query("SELECT u.id as id, u.username as username FROM User u " +
            "JOIN Relationship r ON (r.id.requester = u OR r.id.receiver = u) " +
            "WHERE r.id.receiver.id = :userId AND u.id <> :userId AND r.validatedAt IS NULL")
    List<UserRelationshipProjection> findWaitingUserRelations(@Param("userId") long userId);


    /**
     * Finds a relationship between two users.
     *
     * @param requesterId the ID of the requester
     * @param receiverId the ID of the receiver
     * @return an optional containing the relationship if found, or empty if not found
     */
    @Query("SELECT r FROM Relationship r WHERE (r.id.requester.id = :receiverId AND r.id.receiver.id = :requesterId) OR (r.id.requester.id = :requesterId AND r.id.receiver.id = :receiverId)")
    Optional<Relationship> findByRequesterIdAndReceiverId(@Param("requesterId") long requesterId, @Param("receiverId") long receiverId);
}
