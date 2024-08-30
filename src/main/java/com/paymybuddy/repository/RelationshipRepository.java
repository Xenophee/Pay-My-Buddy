package com.paymybuddy.repository;

import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Relationship r " +
            "WHERE r.id.requester.id = :userId AND r.id.receiver.id = :receiverId")
    boolean existsByUserIdAndReceiverId(@Param("userId") Long userId, @Param("receiverId") Long receiverId);

    @Query("SELECT u.id as id, u.username as username FROM User u " +
            "JOIN Relationship r ON (r.id.requester = u OR r.id.receiver = u) " +
            "WHERE (r.id.requester.id = :userId OR r.id.receiver.id = :userId) AND u.id <> :userId AND r.validatedAt IS NOT NULL")
    List<UserRelationshipProjection> findUserRelations(@Param("userId") Long userId);
}
