package org.example.javablog.controller;

import org.example.javablog.dto.UserDTO;
import org.example.javablog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        try{
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @DeleteMapping("/{deletedUserId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long deletedUserId,
                                            @RequestParam Long userId){
        try{
            userService.deleteUser(deletedUserId, userId);
            return ResponseEntity.noContent().build();
        }
        catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
