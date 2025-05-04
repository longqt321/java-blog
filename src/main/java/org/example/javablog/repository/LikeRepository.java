package org.example.javablog.repository;
import org.example.javablog.model.LikeRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LikeRepository extends JpaRepository<LikeRelationship, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    @Query("SELECT lr.post.id FROM LikeRelationship lr WHERE lr.user.id = :userId")
    Set<Long> findLikedPostIdsByUserId(Long userId);
    void deleteByPostId(Long postId);
}
