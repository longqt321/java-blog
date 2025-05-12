package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.dto.PostFilterRequest;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.constant.Visibility;
import org.example.javablog.repository.PostRepository;
import org.example.javablog.specifications.PostSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    private PostRepository postRepository;

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

        if (!post.getAuthor().getId().equals(userId) || !userService.isAdmin(userId)) {
            throw new SecurityException("User is not authorized to delete this post.");
        }
        blogRepository.delete(post);
    }
    public Page<PostDTO> searchPosts(PostFilterRequest filter, Pageable pageable){
        Specification<Post> spec = PostSpecification.filterBy(filter);
        return postRepository.findAll(spec,pageable).map(PostMapper::toDTO);
    }
}
