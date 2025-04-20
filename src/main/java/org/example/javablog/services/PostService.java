package org.example.javablog.services;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<PostDTO> getPosts(){
        return PostMapper.toDTOList(blogRepository.findAll());
    }

    public PostDTO getPostById(Long id) {
        return PostMapper.toDTO(Objects.requireNonNull(blogRepository.findById(id).orElse(null)));
    }
    public List<PostDTO> getRecommendedPosts(Long userId) {
        List<Post> posts = blogRepository.findAll();
        return PostMapper.toDTOList(posts);
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
    public void deletePost(Long postId, Long userId) {
        Post post = blogRepository.findById(postId).orElseThrow(NullPointerException::new);

        if (!post.getAuthor().getId().equals(userId) && !userService.isAdmin(userId)) {
            throw new SecurityException("User is not authorized to delete this post.");
        }

        blogRepository.delete(post);
    }
}
