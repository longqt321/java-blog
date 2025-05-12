package org.example.javablog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javablog.dto.*;
import org.example.javablog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response){
        try{
            AuthResponse authResponse = authService.login(request);

            Cookie cookie = new Cookie("refresh_token", authResponse.getRefreshToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/api/auth/refresh");
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
            cookie.setSecure(true); // Set to true if using HTTPS
            response.addCookie(cookie);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Auth: " + auth.getAuthorities());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new LoginResponse(authResponse.getAccessToken(),authResponse.getUser()));
        }catch(RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false,e.getMessage(),null));
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

            return ResponseEntity
                    .status(HttpStatus.CONFLICT) //Should be eResponse.getStatusCode()
                    .body(new ApiResponse<>(false,e.getMessage(),null));
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
                    .body(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
}
