package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;



@Data
@Entity
@Table(name = "relationships")
public class Relationship {

    @EmbeddedId
    private RelationshipId id;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;


    @Data
    @Embeddable
    public static class RelationshipId {

        @ManyToOne
        @JoinColumn(name = "requester_id", referencedColumnName = "id")
        private User requester;

        @ManyToOne
        @JoinColumn(name = "receiver_id", referencedColumnName = "id")
        private User receiver;
    }
}
