package org.example.javablog.repository;

import org.example.javablog.model.Post;
import org.example.javablog.constant.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {
    List<Post> findByAuthorId(Long userId);
    Long countByAuthorId(Long userId);
    @Query("SELECT p.author.id FROM Post p WHERE p.id = :postId")
    Long findAuthorIdById(@Param("postId") Long postId);
}
