package org.example.javablog.controller;

import org.example.javablog.dto.FollowRequest;
import org.example.javablog.model.FollowRelationship;
import org.example.javablog.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<Void> followUser(@RequestBody FollowRequest followRequest) {
        followService.followUser(followRequest.getFollowingUserId(), followRequest.getFollowedUserId());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(@RequestBody FollowRequest followRequest) {
        followService.unFollowUser(followRequest.getFollowingUserId(), followRequest.getFollowedUserId());
        return ResponseEntity.ok().build();
    }
}
