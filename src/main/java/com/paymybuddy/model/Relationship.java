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

    /**
     * The timestamp when the relationship was created.
     * This field is automatically populated by the database.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the relationship was validated.
     */
    @Column(name = "validated_at")
    @Setter
    private LocalDateTime validatedAt;
}
