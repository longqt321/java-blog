package org.example.javablog.services;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository blogRepository;

    public List<PostDTO> getPosts(){
        return PostMapper.toDTOList(blogRepository.findAll());
    }

    public Post getPostById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }
    public Post createPost(Post post) {
        System.out.println("Post service:");
        System.out.println(post);
        System.out.println(post.getAuthor());
        return blogRepository.save(post);
    }
}
