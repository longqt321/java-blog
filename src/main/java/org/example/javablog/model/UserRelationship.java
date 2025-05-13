package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.javablog.constant.UserRelationshipType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_relationships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"relationship_type", "user_id", "post_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRelationship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="source_user_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sourceUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_user_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private UserRelationshipType userRelationshipType;

    @PrePersist
    @PreUpdate
    private void validateRelationshipType() {
        if (userRelationshipType == UserRelationshipType.NONE) {
            throw new IllegalArgumentException("Relationship type cannot be NONE");
        }
    }
    public static UserRelationship fromIds(Long sourceUserId, Long targetUserId, UserRelationshipType userRelationshipType) {
        return UserRelationship.builder()
                .sourceUser(User.builder().id(sourceUserId).build())
                .targetUser(User.builder().id(targetUserId).build())
                .userRelationshipType(userRelationshipType)
                .build();
    }
}
