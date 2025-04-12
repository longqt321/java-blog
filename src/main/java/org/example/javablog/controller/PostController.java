package org.example.javablog.controller;


import org.example.javablog.dto.PostDTO;
import org.example.javablog.model.Post;
import org.example.javablog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class PostController {
    @Autowired
    private PostService blogService;

    @GetMapping
    public List<PostDTO> getAllPosts() {
        return blogService.getPosts();
    }
    @GetMapping({"/{id}"})
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = blogService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = blogService.createPost(post);
        System.out.println(createdPost);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Post existingPost = blogService.getPostById(id);
        if (existingPost != null) {
            Post updatedPost = blogService.createPost(post);
            return ResponseEntity.ok(updatedPost);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
