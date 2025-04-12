package org.example.javablog.controller;


import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.PostMapper;
import org.example.javablog.model.Post;
import org.example.javablog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        try {
            PostDTO post = blogService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch(NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = blogService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody Post post) {
        try{
            PostDTO updatedPost = blogService.updatePost(id,post);
            return ResponseEntity.ok(updatedPost);
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
