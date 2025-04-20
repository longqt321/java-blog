package org.example.javablog.controller;

import org.example.javablog.services.BlockService;
import org.example.javablog.services.FollowService;
import org.example.javablog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class BlockController {
    @Autowired
    private BlockService blockService;
    @Autowired
    private UserService userService;

    @PostMapping("/block/{blockedUserId}")
    public ResponseEntity<Void> blockUser(@PathVariable Long blockedUserId, Principal principal) {
        String currentUsername = principal.getName();
        Long blockingUserId = userService.getUserByUsername(currentUsername).getId();
        blockService.blockUser(blockingUserId,blockedUserId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/unblock/{unblockedUserId}")
    public ResponseEntity<Void> unblockUser(@PathVariable Long unblockedUserId, Principal principal) {
        String currentUsername = principal.getName();
        Long unblockingUserId = userService.getUserByUsername(currentUsername).getId();
        blockService.unblockUser(unblockingUserId,unblockedUserId);
        return ResponseEntity.ok().build();
    }
}
