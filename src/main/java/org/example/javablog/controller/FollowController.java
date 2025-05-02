package org.example.javablog.controller;

import org.example.javablog.services.FollowService;
import org.example.javablog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    @PostMapping("/follow/{followedUserId}")
    public ResponseEntity<Void> followUser(@PathVariable Long followedUserId, Principal principal) {
        String currentUsername = principal.getName();
        Long followingUserId = userService.getUserByUsername(currentUsername).getId();
        if (!Objects.equals(followingUserId, followedUserId)){
            followService.followUser(followingUserId, followedUserId);
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/unfollow/{unfollowedUserId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long unfollowedUserId, Principal principal) {
        String currentUsername = principal.getName();
        Long unfollowingUserId = userService.getUserByUsername(currentUsername).getId();
        if (!Objects.equals(unfollowingUserId, unfollowedUserId)){
            followService.unfollowUser(unfollowingUserId, unfollowedUserId);
        }
        return ResponseEntity.ok().build();
    }
}
