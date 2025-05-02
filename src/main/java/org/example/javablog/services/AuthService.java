package org.example.javablog.services;

import org.example.javablog.dto.LoginRequest;
import org.example.javablog.dto.AuthResponse;
import org.example.javablog.dto.RegisterRequest;
import org.example.javablog.dto.RegisterResponse;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.Role;
import org.example.javablog.model.User;
import org.example.javablog.repository.UserRepository;
import org.example.javablog.security.CustomUserDetails;
import org.example.javablog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public RegisterResponse register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setRole(Role.ROLE_USER);
        userRepository.save(newUser);

        return new RegisterResponse("Register successfully");
    }
    public AuthResponse login(LoginRequest request){
        if (!userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Invalid username or password");
        }
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isPresent()){
            if (passwordEncoder.matches(request.getPassword(), user.get().getPassword())){
                UserDetails userDetails = new CustomUserDetails(user.get());
                String accessToken = jwtUtil.generateAccessToken(userDetails);
                String refreshToken = jwtUtil.generateRefreshToken(userDetails);

                return new AuthResponse(accessToken,refreshToken, UserMapper.toDTO(user.get()));
            }else {
                throw new RuntimeException("Invalid username or password");
            }
        }else {
            throw new RuntimeException("Invalid username or password");
        }
    }
    public String refreshAccessToken(String refreshToken){
        if (jwtUtil.validateToken(refreshToken)){
            String username = jwtUtil.extractUsername(refreshToken);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()){
                UserDetails userDetails = new CustomUserDetails(user.get());
                return jwtUtil.generateAccessToken(userDetails);
            }else {
                throw new RuntimeException("Invalid refresh token");
            }
        }else {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
