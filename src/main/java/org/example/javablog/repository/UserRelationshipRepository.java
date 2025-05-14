package org.example.javablog.repository;

import org.example.javablog.constant.UserRelationshipType;
import org.example.javablog.model.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRelationshipRepository  extends JpaRepository<UserRelationship, Long> {
    void deleteBySourceUserIdAndTargetUserIdAndUserRelationshipType(Long sourceId, Long targetId, UserRelationshipType type);
    boolean existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(Long sourceId, Long targetId, UserRelationshipType type);
    Long countBySourceUserIdAndUserRelationshipType(Long sourceId, UserRelationshipType type);
    Long countByTargetUserIdAndUserRelationshipType(Long targetId, UserRelationshipType type);
    Optional<UserRelationship> findBySourceUserIdAndTargetUserId(Long sourceId, Long targetId);
}
