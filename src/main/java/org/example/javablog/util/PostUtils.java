package org.example.javablog.util;

import org.example.javablog.constant.PostRelationshipType;
import org.example.javablog.repository.PostRelationshipRepository;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.example.javablog.dto.PostDTO;

@Component
public class PostUtils {
    @Autowired
    private  PostRepository postRepository;
    @Autowired
    private  PostRelationshipRepository postRelationshipRepository;


    public boolean validateOwnership(Long userId,Long postId){
        if (!postRepository.existsById(postId)){
            return false;
        }
        Long authorId = postRepository.findAuthorIdById(postId);
        return authorId.equals(userId);
    }
    public boolean existsByRelationship(Long userId, Long postId, PostRelationshipType type){
        return postRelationshipRepository.existsByUserIdAndPostIdAndPostRelationshipType(userId,postId,type);
    }
}
