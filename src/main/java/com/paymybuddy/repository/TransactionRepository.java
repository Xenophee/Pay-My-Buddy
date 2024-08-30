package com.paymybuddy.repository;


import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.dto.TransactionProjection;
import com.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

//    @Query("SELECT new com.paymybuddy.dto.TransactionDTO(" +
//            "CASE WHEN t.sender.id = :userId THEN r.username ELSE s.username END, " +
//            "t.sender.id = :userId, " +
//            "t.processedAt, " +
//            "t.amount, " +
//            "t.description) " +
//            "FROM Transaction t " +
//            "JOIN t.sender s " +
//            "JOIN t.receiver r " +
//            "WHERE t.sender.id = :userId OR t.receiver.id = :userId")
//    List<TransactionDTO> findAllByUserIdWithUsernames(@Param("userId") Long userId);

//    @Query("SELECT " +
//            "CASE WHEN t.sender.id = :userId THEN r.username ELSE s.username END AS username, " +
//            "CASE WHEN t.sender.id = :userId THEN true ELSE false END AS isSender, " +
//            "t.processedAt AS processedAt, " +
//            "t.amount AS amount, " +
//            "t.description AS description " +
//            "FROM Transaction t " +
//            "JOIN t.sender s " +
//            "JOIN t.receiver r " +
//            "WHERE t.sender.id = :userId OR t.receiver.id = :userId")
//    List<TransactionProjection> findAllByUserIdWithUsernames(@Param("userId") Long userId);

//    @Query("SELECT " +
//            "CASE WHEN t.sender.id = :userId THEN r.username ELSE s.username END AS username, " +
//            "t.sender.id = :userId AS isSender, " +
//            "t.processedAt AS processedAt, " +
//            "t.amount AS amount, " +
//            "t.description AS description " +
//            "FROM Transaction t " +
//            "JOIN t.sender s " +
//            "JOIN t.receiver r " +
//            "WHERE t.sender.id = :userId OR t.receiver.id = :userId")
//    List<TransactionProjection> findAllByUserIdWithUsernames(@Param("userId") Long userId);

    @Query("SELECT new com.paymybuddy.dto.TransactionDTO(" +
            "CASE WHEN t.sender.id = :userId THEN r.username ELSE s.username END, " +
            "CASE WHEN t.sender.id = :userId THEN true ELSE false END, " +
            "t.processedAt, " +
            "t.amount, " +
            "t.description) " +
            "FROM Transaction t " +
            "JOIN t.sender s " +
            "JOIN t.receiver r " +
            "WHERE t.sender.id = :userId OR t.receiver.id = :userId")
    List<TransactionDTO> findAllByUserIdWithUsernames(@Param("userId") Long userId);

}
