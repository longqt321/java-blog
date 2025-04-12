package org.example.javablog.services;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PostService {

    @Autowired
    private PostRepository blogRepository;

    public List<PostDTO> getPosts(){
        return PostMapper.toDTOList(blogRepository.findAll());
    }

    public PostDTO getPostById(Long id) {
        return PostMapper.toDTO(Objects.requireNonNull(blogRepository.findById(id).orElse(null)));
    }
    public Post createPost(Post post) {
        return blogRepository.save(post);
    }
    public PostDTO updatePost(Long id,Post post){
        Post updatedPost = blogRepository.findById(id).orElseThrow(NullPointerException::new);
        updatedPost.setTitle(post.getTitle());
        updatedPost.setBody(post.getBody());
        updatedPost.setHashtags(post.getHashtags());
        updatedPost.setStatus(post.getStatus());
        return PostMapper.toDTO(blogRepository.save(updatedPost));
    }
}
