package org.example.javablog.repository;

import org.example.javablog.model.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRelationshipRepository  extends JpaRepository<UserRelationship, Long> {
    Optional<UserRelationship> findBySourceUserIdAndTargetUserId(Long sourceId, Long targetId);
    void deleteBySourceUserIdAndTargetUserId(Long sourceId, Long targetId);
}
