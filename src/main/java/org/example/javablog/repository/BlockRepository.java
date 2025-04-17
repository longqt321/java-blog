package org.example.javablog.repository;

import org.example.javablog.model.BlockRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<BlockRelationship,Long> {
    
}
