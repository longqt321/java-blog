package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table (name = "block_relationship")
@Data
public class BlockRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="blocking_user_id",nullable = false)
    private User blockingUser;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id",nullable = false)
    private User blockedUser;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;
}

