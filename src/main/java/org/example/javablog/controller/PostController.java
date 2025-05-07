package org.example.javablog.controller;


import org.example.javablog.dto.PostDTO;
import org.example.javablog.dto.PostFilterRequest;
import org.example.javablog.dto.UserDTO;

import org.example.javablog.services.PostService;
import org.example.javablog.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class PostController {
    @Autowired
    private PostService postService;
 
    @Autowired
    private UserService userService;

    @GetMapping
    public List<PostDTO> getPublicPosts(Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());
        return postService.getPublicPosts(user.getId());
    }
    @GetMapping({"/{id}"})
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        try {
            PostDTO post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch(NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        try {
            List<PostDTO> posts = postService.getPostsByUserId(userId);
            return ResponseEntity.ok(posts);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO post, Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());
        post.setAuthor(user);
        System.out.println(user);
        PostDTO createdPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostDTO post) {
        try{
            PostDTO updatedPost = postService.updatePost(id,post);
            return ResponseEntity.ok(updatedPost);
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Principal principal) {
        try {
            String currentUsername = principal.getName();
            Long userId = userService.getUserByUsername(currentUsername).getId();
            postService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        }
        catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/search")
    public Page<PostDTO> getPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> hashtags,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sortBy,
            Sort sort) {
        PostFilterRequest filter = new PostFilterRequest();
        filter.setTitle(title);
        filter.setHashtags(hashtags);
        filter.setAuthorName(author);

        String prop = sortBy.split(",")[0];
        String direction = sortBy.split(",")[1];

        Sort sorter = Sort.by(new Sort.Order(Sort.Direction.fromString(direction), prop));

        Pageable pageable = PageRequest.of(page, size, sorter);
        return postService.searchPosts(filter, pageable);
    }
}
