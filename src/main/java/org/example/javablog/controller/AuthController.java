package org.example.javablog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javablog.dto.*;
import org.example.javablog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @GetMapping("/")
    public String index(){
        return "Test api";
    }
    @GetMapping("/testroute")
    public String test(){

        return "Test route";
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response){
        try{
            LoginResponse authResponse = authService.login(request);

            Cookie cookie = new Cookie("refresh_token", authResponse.getRefreshToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/api/auth/refresh");
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
            cookie.setSecure(true); // Set to true if using HTTPS
            response.addCookie(cookie);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TokenResponse(authResponse.getAccessToken()));
        }catch(RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        try{
            RegisterResponse response = authService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }catch(RuntimeException e) {
            ErrorResponse eResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) //Should be eResponse.getStatusCode()
                    .body(eResponse);
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refresh_token", defaultValue = "") String refreshToken){
        try{
            String accessToken = authService.refreshAccessToken(refreshToken);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TokenResponse(accessToken));
        }catch(RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
