package org.example.javablog.repository;

import org.example.javablog.model.PostRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.javablog.constant.PostRelationshipType;

@Repository
public interface PostRelationshipRepository extends JpaRepository<PostRelationship, Long> {
    void deleteByUserIdAndPostIdAndPostRelationshipType(Long userId,Long postId,PostRelationshipType type);
    boolean existsByUserIdAndPostIdAndPostRelationshipType(Long userId,Long postId,PostRelationshipType type);
}
