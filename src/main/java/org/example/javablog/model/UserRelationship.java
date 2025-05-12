package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.javablog.constant.UserRelationshipType;

@Entity
@Table(name = "user_relationships")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRelationship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="source_user_id",nullable = false)
    private User sourceUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_user_id",nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    private UserRelationshipType userRelationshipType;

    @PrePersist
    @PreUpdate
    private void validateRelationshipType() {
        if (userRelationshipType == UserRelationshipType.NONE) {
            throw new IllegalArgumentException("Relationship type cannot be NONE");
        }
    }
}
