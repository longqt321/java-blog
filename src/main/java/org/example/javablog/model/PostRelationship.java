package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.javablog.constant.PostRelationshipType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.mapping.Join;

@Entity
@Table(name = "post_relationships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"relationship_type", "user_id", "post_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRelationship extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private PostRelationshipType postRelationshipType;

    @PrePersist
    @PreUpdate
    private void validateRelationshipType(){
        if (postRelationshipType == PostRelationshipType.NONE){
            throw new IllegalArgumentException("Relationship type cannot be NONE");
        }
    }
    public static PostRelationship fromIds(Long userId, Long postId, PostRelationshipType postRelationshipType) {
        return PostRelationship.builder()
                .user(User.builder().id(userId).build())
                .post(Post.builder().id(postId).build())
                .postRelationshipType(postRelationshipType)
                .build();
    }

}
