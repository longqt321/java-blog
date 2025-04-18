package org.example.javablog.repository;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
}
