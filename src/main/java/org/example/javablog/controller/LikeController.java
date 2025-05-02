package org.example.javablog.controller;

import org.example.javablog.services.LikeService;
import org.example.javablog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/blogs")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;

    @PostMapping("/like/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, Principal principal) {
        String currentUsername = principal.getName();
        Long userId = userService.getUserByUsername(currentUsername).getId();
        likeService.likePost(userId, postId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/unlike/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, Principal principal) {
        String currentUsername = principal.getName();
        Long userId = userService.getUserByUsername(currentUsername).getId();
        likeService.unlikePost(userId, postId);
        return ResponseEntity.ok().build();
    }
}
