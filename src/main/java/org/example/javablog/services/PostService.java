package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.constant.PostRelationshipType;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.dto.PostFilterRequest;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.constant.Visibility;
import org.example.javablog.model.PostRelationship;
import org.example.javablog.repository.PostRelationshipRepository;
import org.example.javablog.repository.PostRepository;
import org.example.javablog.specifications.PostSpecification;
import org.example.javablog.util.PostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    private PostRepository postRepository;

    @Autowired
    private PostRelationshipRepository postRelationshipRepository;

    @Autowired
    private PostUtils postUtils;

    public PostDTO getPostById(Long id) {
        return PostMapper.toDTO(Objects.requireNonNull(blogRepository.findById(id).orElse(null)));
    }
    public List<PostDTO> getPostsByAuthorId(Long id){
        return PostMapper.toDTOList(Objects.requireNonNull(blogRepository.findByAuthorId(id)));
    }
    public PostDTO createPost(PostDTO postDTO) {
        Post newPost = new Post();
        newPost.setTitle(postDTO.getTitle());
        newPost.setBody(postDTO.getBody());
        newPost.setVisibility(postDTO.getVisibility());
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
        updatedPost.setVisibility(postDTO.getVisibility());

        updatedPost.setHashtags(postDTO.getHashtags().stream()
                .map(hashtagName-> hashtagService.getOrCreateHashtag(hashtagName))
                .collect(Collectors.toSet()));

        return PostMapper.toDTO(blogRepository.save(updatedPost));
    }
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = blogRepository.findById(postId).orElseThrow(NullPointerException::new);

        if (!post.getAuthor().getId().equals(userId) || !userService.isAdmin(userId)) {
            throw new SecurityException("User is not authorized to delete this post.");
        }
        blogRepository.delete(post);
    }
    public Page<PostDTO> searchPosts(PostFilterRequest filter, Pageable pageable){
        Long userId = userService.getCurrentUser().getId();
        if (filter.getAuthorId() == null || !userId.equals(filter.getAuthorId())){ // Neu user khong phai author thi chi return public posts
            filter.setVisibility(String.valueOf(Visibility.PUBLIC));
        }
        Specification<Post> spec = PostSpecification.filterBy(filter);
        return postRepository.findAll(spec,pageable).map(PostMapper::toDTO);
    }

    public void likePost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.LIKED)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot like your own posts");
        }
        postRelationshipRepository.save(PostRelationship.fromIds(userId, postId, PostRelationshipType.LIKED));
    }
    @Transactional
    public void unlikePost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.LIKED);
    }
    public void hidePost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.HIDDEN)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot hide your own posts");
        }
        postRelationshipRepository.save(PostRelationship.fromIds(userId,postId,PostRelationshipType.HIDDEN));
    }
    @Transactional
    public void unhidePost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.HIDDEN);
    }
    public void reportPost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.REPORTED)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot report your own posts");
        }
        postRelationshipRepository.save(PostRelationship.fromIds(userId,postId,PostRelationshipType.REPORTED));
    }
    @Transactional
    public void unreportPost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.REPORTED);
    }
    public void savePost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.SAVED)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot save your own posts");
        }
        postRelationshipRepository.save(PostRelationship.fromIds(userId,postId,PostRelationshipType.SAVED));
    }
    @Transactional
    public void unsavePost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.SAVED);
    }
}
