package org.example.javablog.repository;

import org.example.javablog.model.BlockRelationship;
import org.example.javablog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<BlockRelationship,Long> {
    void deleteByBlockingUserAndBlockedUser(User blocking_user, User blocked_user);
    boolean existsByBlockingUserAndBlockedUser(User blocking_user, User blocked_user);
}
