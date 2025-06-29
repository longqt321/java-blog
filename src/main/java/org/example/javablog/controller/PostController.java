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

import java.io.IOException;
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
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> hashtags,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String relationshipType,
            @RequestParam(required = false) Boolean excludeHidden,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sortBy
    ) {

        try{
            PostFilterRequest filter = new PostFilterRequest();
            filter.setId(id);
            filter.setTitle(title);
            filter.setHashtags(hashtags);
            filter.setAuthorName(author);
            filter.setUserId(userId);
            filter.setAuthorId(authorId);
            filter.setVisibility(visibility);
            filter.setRelationshipType(relationshipType);
            filter.setExcludeHidden(excludeHidden != null ? excludeHidden : true);
            filter.setAuthorUsername(username);

            String[] sortParams = sortBy.split(",");
            Sort sort = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);


            Pageable pageable = PageRequest.of(page, size,sort);
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
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            PostDTO post = postService.getPostById(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Post retrieved successfully", post));
        } catch(NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Post not found", null));
        }
    }
    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestBody PostDTO post) {
        UserDTO user = userService.getCurrentUser();
        post.setAuthor(user);
        PostDTO createdPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Post created successfully", createdPost));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostDTO post) {
        try{
            PostDTO updatedPost = postService.updatePost(id,post);
            return ResponseEntity.ok(new ApiResponse<>(true, "Post updated successfully", updatedPost));
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
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.likePost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Liked successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.unlikePost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Unliked successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{postId}/hide")
    public ResponseEntity<?> hidePost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.hidePost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Hide post successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @DeleteMapping("/{postId}/hide")
    public ResponseEntity<?> unhidePost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.unhidePost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Unhide post successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{postId}/report")
    public ResponseEntity<?> reportPost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.reportPost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Report post successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @DeleteMapping("/{postId}/report")
    public ResponseEntity<?> unreportPost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.unreportPost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Unreport post successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{postId}/save")
    public ResponseEntity<?> savePost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.savePost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Save post successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @DeleteMapping("/{postId}/save")
    public ResponseEntity<?> unsavePost(@PathVariable Long postId){
        try{
            Long userId = userService.getCurrentUser().getId();
            postService.unsavePost(userId,postId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Unsave post successfully",null));
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }

}
