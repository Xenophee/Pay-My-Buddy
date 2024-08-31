package com.paymybuddy.repository;

import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Relationship r " +
            "WHERE r.id.requester.id = :userId AND r.id.receiver.id = :receiverId AND r.validatedAt IS NOT NULL")
    boolean existsByUserIdAndReceiverId(@Param("userId") long userId, @Param("receiverId") long receiverId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Relationship r " +
            "WHERE (r.id.requester.id = :receiverId AND r.id.receiver.id = :userId) OR (r.id.requester.id = :userId AND r.id.receiver.id = :receiverId) AND r.validatedAt IS NULL")
    boolean existsWaitingByUserIdAndReceiverId(long userId, long receiverId);

    @Query("SELECT u.id as id, u.username as username FROM User u " +
            "JOIN Relationship r ON (r.id.requester = u OR r.id.receiver = u) " +
            "WHERE (r.id.requester.id = :userId OR r.id.receiver.id = :userId) AND u.id <> :userId AND r.validatedAt IS NOT NULL")
    List<UserRelationshipProjection> findUserRelations(@Param("userId") long userId);

    @Query("SELECT u.id as id, u.username as username FROM User u " +
            "JOIN Relationship r ON (r.id.requester = u OR r.id.receiver = u) " +
            "WHERE r.id.receiver.id = :userId AND u.id <> :userId AND r.validatedAt IS NULL")
    List<UserRelationshipProjection> findWaitingUserRelations(@Param("userId") long userId);

    @Query("SELECT r FROM Relationship r WHERE (r.id.requester.id = :receiverId AND r.id.receiver.id = :requesterId) OR (r.id.requester.id = :requesterId AND r.id.receiver.id = :receiverId)")
    Optional<Relationship> findByRequesterIdAndReceiverId(@Param("requesterId") long requesterId, @Param("receiverId") long receiverId);
}
