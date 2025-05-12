package org.example.javablog.repository;

import org.example.javablog.model.PostRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRelationshipRepository extends JpaRepository<PostRelationship, Long> {

}
