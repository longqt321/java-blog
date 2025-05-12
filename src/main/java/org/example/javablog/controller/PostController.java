package org.example.javablog.controller;


import org.example.javablog.dto.*;

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
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> hashtags,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sortBy,
            Sort sort) {
        PostFilterRequest filter = new PostFilterRequest();
        filter.setTitle(title);
        filter.setHashtags(hashtags);
        filter.setAuthorName(author);
        filter.setUsername(username);
        filter.setVisibility(visibility);

        String prop = sortBy.split(",")[0];
        String direction = sortBy.split(",")[1];

        Sort sorter = Sort.by(new Sort.Order(Sort.Direction.fromString(direction), prop));

        Pageable pageable = PageRequest.of(page, size, sorter);
        Page<PostDTO> postPage = postService.searchPosts(filter, pageable);
        PageResponse<PostDTO> pageResponse = new PageResponse<>(postPage.getContent(),
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isFirst(),
                postPage.isLast());
        ApiResponse<PageResponse<PostDTO>> response = new ApiResponse<>(true, "Posts retrieved successfully",pageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            PostDTO post = postService.getPostById(id);

            return ResponseEntity.ok(new ApiResponse<PostDTO>(true, "Post retrieved successfully", post));
        } catch(NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Post not found", null));
        }
    }
    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestBody PostDTO post) {
        UserDTO user = userService.getCurrentUser();
        post.setAuthor(user);
        PostDTO createdPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<PostDTO>(true, "Post created successfully", createdPost));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostDTO post) {
        try{
            PostDTO updatedPost = postService.updatePost(id,post);
            return ResponseEntity.ok(new ApiResponse<PostDTO>(true, "Post updated successfully", updatedPost));
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Post not found", null));
        }
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            Long userId = userService.getCurrentUser().getId();
            postService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        }
        catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(false,"You are not authorized to delete this post", null));
        }
        catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Post not found", null));
        }
    }

}
