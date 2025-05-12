package org.example.javablog.services;

import org.example.javablog.constant.PostRelationshipType;
import org.example.javablog.model.PostRelationship;
import org.example.javablog.model.User;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.javablog.repository.PostRelationshipRepository;

@Service
public class PostRelationshipService {
    @Autowired
    private PostRelationshipRepository postRelationshipRepository;

    @Autowired
    private PostRepository postRepository;

    public void likePost(Long userId,Long postId){
        Long authorId = postRepository.findAuthorIdById(postId);
        if (authorId == null) {
            throw new IllegalArgumentException("Post not found");
        }
        if (authorId.equals(userId)) {
            throw new IllegalArgumentException("You cannot like your own post.");
        }
        postRelationshipRepository.save(PostRelationship.fromIds(userId, postId, PostRelationshipType.LIKED));
    }
}
