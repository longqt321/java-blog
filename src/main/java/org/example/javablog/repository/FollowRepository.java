package org.example.javablog.repository;

import org.example.javablog.model.FollowRelationship;
import org.example.javablog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowRelationship, Long> {
    void deleteByFollowingUserAndFollowedUser(User following_user, User followed_user);
    boolean existsByFollowingUserAndFollowedUser(User following_user, User followed_user);
}
