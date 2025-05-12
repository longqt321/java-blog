package org.example.javablog.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.javablog.constant.PostRelationshipType;
import org.example.javablog.repository.PostRelationshipRepository;
import org.example.javablog.repository.PostRepository;
import org.springframework.stereotype.Component;

@Component
public class PostUtil {
    private final PostRepository postRepository;
    private final PostRelationshipRepository postRelationshipRepository;

    public PostUtil(PostRepository postRepository, PostRelationshipRepository postRelationshipRepository){
        this.postRepository = postRepository;
        this.postRelationshipRepository = postRelationshipRepository;
    }

    public boolean exists(Long postId){
        return postRepository.existsById(postId);
    }
    public boolean validateOwnership(Long userId,Long postId){
        if (!this.exists(postId)){
            return false;
        }
        Long authorId = postRepository.findAuthorIdById(postId);
        return authorId.equals(userId);
    }
    public boolean existsByRelationship(Long userId, Long postId, PostRelationshipType type){
        return postRelationshipRepository.existsByUserIdAndPostIdAndPostRelationshipType(userId,postId,type);
    }
}
