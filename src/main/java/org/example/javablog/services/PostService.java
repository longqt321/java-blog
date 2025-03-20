package org.example.javablog.services;

import org.example.javablog.model.Post;
import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository blogRepository;

    public List<Post> getPosts(){
        return blogRepository.findAll();
    }
}
