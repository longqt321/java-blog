package org.example.javablog.controller;


import org.example.javablog.model.Post;
import org.example.javablog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class PostController {
    @Autowired
    private PostService blogService;

    @GetMapping
    public List<Post> getAllPosts() {
        return blogService.getPosts();
    }
}
