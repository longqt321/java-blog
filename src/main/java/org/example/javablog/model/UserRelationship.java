package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.javablog.constant.Relationship;

@Entity
@Table(name = "relationships")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRelationship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="source_user_id",nullable = false)
    private User sourceUser;

    @ManyToOne
    @JoinColumn(name = "target_user_id",nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    private Relationship relationship;

}
