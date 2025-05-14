package org.example.javablog.repository;

import org.example.javablog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    @Query("SELECT ur.targetUser.id, COUNT(ur) FROM UserRelationship ur " +
            "WHERE ur.userRelationshipType = 'FOLLOWING' AND ur.targetUser.id IN :userIds " +
            "GROUP BY ur.targetUser.id")
    List<Object[]> countFollowersByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT ur.sourceUser.id, COUNT(ur) FROM UserRelationship ur " +
            "WHERE ur.userRelationshipType = 'FOLLOWING' AND ur.sourceUser.id IN :userIds " +
            "GROUP BY ur.sourceUser.id")
    List<Object[]> countFollowingByUserIds(@Param("userIds") List<Long> userIds);
}
