package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.constant.PostRelationshipType;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.dto.PostFilterRequest;
import org.example.javablog.dto.PostRelationshipDTO;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.model.PostRelationship;
import org.example.javablog.constant.Visibility;
import org.example.javablog.repository.PostRelationshipRepository;
import org.example.javablog.repository.PostRepository;
import org.example.javablog.repository.RecommendScoreRepository;
import org.example.javablog.specifications.PostSpecification;
import org.example.javablog.util.PostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
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
    private RecommendScoreRepository recommendScoreRepository;

    @Autowired
    private PostUtils postUtils;



    public List<PostDTO> getAllPosts() {
        List<PostDTO> posts = blogRepository.findAll().stream()
                .map(PostMapper::toDTO)
                .collect(Collectors.toList());
        List<Long> postIds = posts.stream()
                .map(PostDTO::getId)
                .toList();

        List<PostRelationship> postRelationships = postRelationshipRepository.findByUserIdAndPostIdIn(userService.getCurrentUser().getId(),postIds);

        Map<Long, PostRelationshipDTO> relMap = postRelationships.stream()
                .collect(Collectors.groupingBy(
                        pr -> pr.getPost().getId(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(PostRelationship::getPostRelationshipType, Collectors.toSet()),
                                set -> new PostRelationshipDTO(
                                        set.contains(PostRelationshipType.LIKED),
                                        set.contains(PostRelationshipType.SAVED),
                                        set.contains(PostRelationshipType.HIDDEN),
                                        set.contains(PostRelationshipType.REPORTED)
                                )
                        )));

        posts.forEach(dto -> {
            dto.setRelationship(relMap.getOrDefault(dto.getId(), new PostRelationshipDTO(false,false,false,false)));
        });
        return posts;
    }

    public PostDTO getPostById(Long id) {
        return PostMapper.toDTO(Objects.requireNonNull(blogRepository.findById(id).orElse(null)));
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


        if (!post.getAuthor().getId().equals(userId) && !userService.isAdmin()) {
            throw new SecurityException("User is not authorized to delete this post.");
        }
        recommendScoreRepository.deleteByPostId(postId);
        blogRepository.delete(post);
    }
    public Page<PostDTO> searchPosts(PostFilterRequest filter, Pageable pageable) throws IOException {

        Long userId = userService.getCurrentUser().getId();
        if (filter.getAuthorId() == null){
            filter.setVisibility(String.valueOf(Visibility.PUBLIC));
        }
        else{
            if (!userId.equals(filter.getAuthorId())) {
                filter.setVisibility(String.valueOf(Visibility.PUBLIC));
            }
        }


        Specification<Post> spec = Specification.where(PostSpecification.filterBy(filter))
                .and(PostSpecification.sortByRecommendScore(userId));


        Page<PostDTO> posts = postRepository.findAll(spec,pageable).map(PostMapper::toDTO);


        List<Long> postIds = posts.getContent().stream()
                .map(PostDTO::getId)
                .toList();
        List<PostRelationship> postRelationships = postRelationshipRepository.findByUserIdAndPostIdIn(userService.getCurrentUser().getId(),postIds);

        Map<Long, PostRelationshipDTO> relMap = postRelationships.stream()
                .collect(Collectors.groupingBy(
                        pr -> pr.getPost().getId(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(PostRelationship::getPostRelationshipType, Collectors.toSet()),
                                set -> new PostRelationshipDTO(
                                        set.contains(PostRelationshipType.LIKED),
                                        set.contains(PostRelationshipType.SAVED),
                                        set.contains(PostRelationshipType.HIDDEN),
                                        set.contains(PostRelationshipType.REPORTED)
                                )
                        )));

        posts.forEach(dto -> {
            dto.setRelationship(relMap.getOrDefault(dto.getId(), new PostRelationshipDTO(false,false,false,false)));
        });

        return posts;
    }

    public void likePost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.LIKED)){
            throw new IllegalArgumentException("You have already liked this post");
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot like your own posts");
        }
        postRelationshipRepository.save(PostRelationship.fromIds(userId, postId, PostRelationshipType.LIKED));
    }
    @Transactional
    public void unlikePost(Long userId,Long postId){
        if (!postUtils.existsByRelationship(userId,postId,PostRelationshipType.LIKED)){
            throw new IllegalArgumentException("You have not liked this post yet");
        }
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.LIKED);
    }
    @Transactional
    public void hidePost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.HIDDEN)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot hide your own posts");
        }
        this.unlikePost(userId,postId); // Unliking the post if it was liked
        this.unsavePost(userId,postId); // Unsaving the post if it was saved
        postRelationshipRepository.save(PostRelationship.fromIds(userId,postId,PostRelationshipType.HIDDEN));
    }
    @Transactional
    public void unhidePost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.HIDDEN);
    }
    @Transactional
    public void reportPost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.REPORTED)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot report your own posts");
        }
        this.unlikePost(userId,postId); // Unliking the post if it was liked
        this.unsavePost(userId,postId); // Unsaving the post if it was saved
        postRelationshipRepository.save(PostRelationship.fromIds(userId,postId,PostRelationshipType.REPORTED));
    }
    @Transactional
    public void unreportPost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.REPORTED);
    }
    @Transactional
    public void savePost(Long userId,Long postId){
        if (postUtils.existsByRelationship(userId,postId,PostRelationshipType.SAVED)){
            return;
        }
        if (postUtils.validateOwnership(userId,postId)){
            throw new IllegalArgumentException("You cannot save your own posts");
        }
        this.unhidePost(userId,postId); // Unhiding the post if it was hidden
        postRelationshipRepository.save(PostRelationship.fromIds(userId,postId,PostRelationshipType.SAVED));
    }
    @Transactional
    public void unsavePost(Long userId,Long postId){
        postRelationshipRepository.deleteByUserIdAndPostIdAndPostRelationshipType(userId,postId,PostRelationshipType.SAVED);
    }
}
