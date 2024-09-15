package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;



@Getter
@Entity
@Table(name = "relationships")
public class Relationship {

    @EmbeddedId
    @Setter
    private RelationshipId id;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "validated_at")
    @Setter
    private LocalDateTime validatedAt;


    @Getter
    @Setter
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
