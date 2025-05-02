package org.example.javablog.repository;

import org.example.javablog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByAuthorId(Long userId);
    @Query("""
    SELECT p, 
           CASE WHEN lr.id IS NOT NULL THEN true ELSE false END
    FROM Post p
    LEFT JOIN LikeRelationship lr ON lr.post.id = p.id AND lr.user.id = :userId
    """)
    List<Object[]> findAllWithLikeStatus(@Param("userId") Long userId);

}
