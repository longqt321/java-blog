package org.example.javablog.controller;

import org.example.javablog.services.PostRelationshipService;
import org.example.javablog.services.UserService;
import org.example.javablog.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blogs")
public class PostRelationshipController {
    @Autowired
    private PostRelationshipService postRelationshipService;

    @Autowired
    private UserService userService;

    @PostMapping("/like/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        Long userId = userService.getCurrentUser().getId();
        postRelationshipService.likePost(userId, postId);
        return ResponseEntity.ok().build();
    }
}
