package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;



@Data
@Entity
@Table(name = "connections")
public class Connection {

    @EmbeddedId
    private ConnectionId id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Embeddable
    public static class ConnectionId {
        @Column(name = "requester_id")
        private int requesterId;

        @Column(name = "receiver_id")
        private int receiverId;
    }
}
