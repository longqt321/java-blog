package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.dto.UserDTO;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.LikeRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.javablog.repository.LikeRepository;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    private boolean isPostLikedByUser(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
    public void likePost(Long userId, Long postId) {
        if (!isPostLikedByUser(userId, postId)) {
            LikeRelationship like = new LikeRelationship();
            UserDTO user = userService.getUserById(userId);
            PostDTO post = postService.getPostById(postId);
            like.setUser(UserMapper.toEntity(user));
            like.setPost(PostMapper.toEntity(post));
            likeRepository.save(like);
        }
    }
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        if (isPostLikedByUser(userId, postId)) {
            System.out.println("unlikePost");
            likeRepository.deleteByUserIdAndPostId(userId, postId);
        }
    }
}
