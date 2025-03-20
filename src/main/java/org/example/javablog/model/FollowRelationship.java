package org.example.javablog.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;

@Entity
@Table (name = "follow_relationship")
public class FollowRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="following_user_id",nullable = false)
    private User following_user;

    @ManyToOne
    @JoinColumn(name = "followed_user_id",nullable = false)
    private User followed_user;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp created_at;
}
