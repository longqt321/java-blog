package org.example.javablog.controller;

import org.example.javablog.constant.Relationship;
import org.example.javablog.model.UserRelationship;
import org.example.javablog.services.UserRelationshipService;
import org.example.javablog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user-relationships")
public class UserRelationshipController {
    @Autowired
    private UserRelationshipService userRelationshipService;

    @Autowired
    private UserService userService;

    @GetMapping("/{targetId}")
    public Relationship getUserRelationships(@PathVariable Long targetId) {
        return userRelationshipService.getRelationship(userService.getCurrentUser().getId(), targetId);
    }
    @PostMapping("/follow/{targetId}")
    public ResponseEntity<String> followUser(@PathVariable Long targetId) {
        Long sourceId = userService.getCurrentUser().getId();
        if (sourceId.equals(targetId)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself.");
        }
        userRelationshipService.follow(sourceId, targetId);
        return ResponseEntity.ok("Followed successfully.");
    }
    @PostMapping("/block/{targetId}")
    public ResponseEntity<String> blockUser(@PathVariable Long targetId) {
        Long sourceId = userService.getCurrentUser().getId();
        if (sourceId.equals(targetId)) {
            return ResponseEntity.badRequest().body("You cannot block yourself.");
        }
        userRelationshipService.block(sourceId, targetId);
        return ResponseEntity.ok("Blocked successfully.");
    }
    @DeleteMapping("/reset/{targetId}")
    public ResponseEntity<String> resetRelationship(@PathVariable Long targetId) {
        Long sourceId = userService.getCurrentUser().getId();
        if (sourceId.equals(targetId)) {
            return ResponseEntity.badRequest().body("You cannot reset your relationship with yourself.");
        }
        userRelationshipService.resetRelationship(sourceId, targetId);
        return ResponseEntity.ok("Relationship reset successfully.");
    }
}
