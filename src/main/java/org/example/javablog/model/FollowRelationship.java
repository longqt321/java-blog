package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.security.Timestamp;

@Entity
@Table (name = "follow_relationship")
@IdClass(FollowId.class)
public class FollowRelationship {

    @Id
    @ManyToOne
    @JoinColumn(name="following_user_id",nullable = false)
    private User following_user;

    @Id
    @ManyToOne
    @JoinColumn(name = "followed_user_id",nullable = false)
    private User followed_user;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp created_at;
}

@Data
class FollowId implements Serializable {
    private Long following_user;
    private Long followed_user;
}
