package com.paymybuddy.repository;


import com.paymybuddy.dto.UserRelationshipProjection;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT u.id as id, u.username as username FROM User u " +
            "JOIN Relationship r ON (r.id.requester = u OR r.id.receiver = u) " +
            "WHERE :userId IN (r.id.requester.id, r.id.receiver.id)")
    List<UserRelationshipProjection> findUserRelations(@Param("userId") Long userId);
}
