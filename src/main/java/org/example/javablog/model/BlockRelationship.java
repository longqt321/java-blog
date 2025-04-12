package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.security.Timestamp;

@Entity
@Table (name = "block_relationship")
@IdClass(BlockId.class)
public class BlockRelationship {

    @Id
    @ManyToOne
    @JoinColumn(name="blocking_user_id",nullable = false)
    private User blocking_user;

    @Id
    @ManyToOne
    @JoinColumn(name = "blocked_user_id",nullable = false)
    private User blocked_user;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp created_at;
}

@Data
class BlockId implements Serializable {
    private Long blocking_user;
    private Long blocked_user;
}
