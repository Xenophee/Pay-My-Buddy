package com.paymybuddy.repository;


import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT " +
            "CASE WHEN t.sender.id = :userId THEN r.username ELSE s.username END AS username, " +
            "CASE WHEN t.sender.id = :userId THEN true ELSE false END AS isSender, " +
            "TO_CHAR(t.processedAt, 'DD/MM/YYYY à HH24hMI') AS processedAt, " +
            "t.amount AS amount, " +
            "t.description AS description " +
            "FROM Transaction t " +
            "JOIN t.sender s " +
            "JOIN t.receiver r " +
            "WHERE t.sender.id = :userId OR t.receiver.id = :userId " +
            "ORDER BY t.processedAt DESC")
    List<TransactionProjection> findAllByUserIdWithUsernames(@Param("userId") Long userId);

}
