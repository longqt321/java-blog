package org.example.javablog.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;

@Entity
@Table (name = "follow_relationship")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="following_user_id",nullable = false)
    private User followingUser;

    @ManyToOne
    @JoinColumn(name = "followed_user_id",nullable = false)
    private User followedUser;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;
}
