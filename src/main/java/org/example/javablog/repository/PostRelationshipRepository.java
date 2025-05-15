package org.example.javablog.repository;

import org.example.javablog.model.PostRelationship;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.javablog.constant.PostRelationshipType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRelationshipRepository extends JpaRepository<PostRelationship, Long> {
    void deleteByUserIdAndPostIdAndPostRelationshipType(Long userId,Long postId,PostRelationshipType type);
    boolean existsByUserIdAndPostIdAndPostRelationshipType(Long userId,Long postId,PostRelationshipType type);
    List<PostRelationship> findByUserIdAndPostIdIn(Long user_id, Collection<Long> postIds);
}
