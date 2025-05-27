package org.example.javablog.controller;

import org.apache.coyote.Response;
import org.example.javablog.dto.ApiResponse;
import org.example.javablog.dto.UserDTO;
import org.example.javablog.services.PostService;
import org.example.javablog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @GetMapping
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers( @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok().body(new ApiResponse<>(
                    true,
                    "Users retrieved successfully",
                    userService.searchUsers(pageable)
            ));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

//    @GetMapping("/current")
//    public ResponseEntity<?> getCurrentUser(){
//        try{
//            return ResponseEntity.ok().body(new ApiResponse<>(true,
//                    "Current logged in user retrieved successfully",
//                    userService.getCurrentUser()
//                    ));
//        }catch(NullPointerException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"Unable to retrieve current logged in user",null));
//        }
//    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        try{
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{deletedUserId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long deletedUserId){
        try{
            userService.deleteUser(deletedUserId);
            return ResponseEntity.noContent().build();
        }
        catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(false,e.getMessage(),null));
        }
        catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO user){
        try{
            UserDTO updatedUser = userService.updateUser(userId,user);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"User updated successfully",updatedUser));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{targetId}/follow")
    public ResponseEntity<?> followUser(@PathVariable Long targetId){
        try{
            Long sourceId = userService.getCurrentUser().getId();
            userService.followUser(sourceId,targetId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Followed successfully",null));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{targetId}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long targetId){
        try {
            Long sourceId = userService.getCurrentUser().getId();
            userService.unfollowUser(sourceId, targetId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Unfollowed successfully",null));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{targetId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long targetId){
        try{
            Long sourceId = userService.getCurrentUser().getId();
            userService.blockUser(sourceId,targetId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Blocked successfully",null));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @PostMapping("/{targetId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long targetId){
        try{
            Long sourceId = userService.getCurrentUser().getId();
            userService.unblockUser(sourceId,targetId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Unblocked successfully",null));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowerCount(@PathVariable Long userId){
        try{
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Follower count retrieved successfully",userService.getFollowerCount(userId)));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowingCount(@PathVariable Long userId){
        try{
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Following count retrieved successfully",userService.getFollowingCount(userId)));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @GetMapping("/relationship/{targetId}")
    public ResponseEntity<?> getUserRelationships(@PathVariable Long targetId){
        try{
            Long sourceId = userService.getCurrentUser().getId();
            return ResponseEntity.ok().body(new ApiResponse<>(true,"User relationships retrieved successfully",userService.getUserRelationship(sourceId,targetId)));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
}
