package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.repository.LikeRepository;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository blogRepository;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeRepository likeRepository;

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<PostDTO> getPosts(Long userId){
        List<PostDTO> posts = PostMapper.toDTOList(blogRepository.findAll());
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserId(userId);
        return posts.stream().map(post -> new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getAuthor(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getHashtags(),
                likedPostIds.contains(post.getId())
        )).collect(Collectors.toList());
    }
    public List<PostDTO> getPostsByUserId(Long userId) {
        List<PostDTO> posts = PostMapper.toDTOList(blogRepository.findByAuthorId(userId));
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserId(userId);
        return posts.stream().map(post -> new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getAuthor(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getHashtags(),
                likedPostIds.contains(post.getId())
        )).collect(Collectors.toList());
    }

    public PostDTO getPostById(Long id) {
        return PostMapper.toDTO(Objects.requireNonNull(blogRepository.findById(id).orElse(null)));
    }
    public PostDTO createPost(PostDTO postDTO) {
        Post newPost = new Post();
        newPost.setTitle(postDTO.getTitle());
        newPost.setBody(postDTO.getBody());
        newPost.setStatus(postDTO.getStatus());
        newPost.setAuthor(UserMapper.toEntity(postDTO.getAuthor()));
        newPost.setHashtags(postDTO.getHashtags().stream()
                .map(hashtagName -> hashtagService.getOrCreateHashtag(hashtagName))
                .collect(Collectors.toSet()));
        return PostMapper.toDTO(blogRepository.save(newPost));
    }
    public PostDTO updatePost(Long id,PostDTO postDTO){
        Post updatedPost = blogRepository.findById(id).orElseThrow(NullPointerException::new);
        updatedPost.setTitle(postDTO.getTitle());
        updatedPost.setBody(postDTO.getBody());
        updatedPost.setStatus(postDTO.getStatus());

        updatedPost.setHashtags(postDTO.getHashtags().stream()
                .map(hashtagName-> hashtagService.getOrCreateHashtag(hashtagName))
                .collect(Collectors.toSet()));

        return PostMapper.toDTO(blogRepository.save(updatedPost));
    }
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = blogRepository.findById(postId).orElseThrow(NullPointerException::new);

        if (!post.getAuthor().getId().equals(userId) && !userService.isAdmin(userId)) {
            throw new SecurityException("User is not authorized to delete this post.");
        }
        likeRepository.deleteByPostId(postId);
        blogRepository.delete(post);
    }

}
