package org.example.javablog.services;

import jakarta.mail.MessagingException;
import org.example.javablog.dto.*;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.constant.Role;
import org.example.javablog.model.Image;
import org.example.javablog.model.User;
import org.example.javablog.repository.UserRepository;
import org.example.javablog.security.CustomUserDetails;
import org.example.javablog.security.JwtUtil;
import org.example.javablog.security.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private OtpUtils otpUtils;
    @Autowired
    private ImageService imageService;

    @Autowired
    private EmailService emailService;

    public void resetPassword(ResetPasswordRequest request) throws IOException, MessagingException {
        if (!userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("User not found");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("New password and confirm new password do not match");
        }
        String email = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getEmail();
        if (!otpUtils.verifyOtp(email, request.getOtpCode())) {
            throw new RuntimeException("Invalid OTP code");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void confirmEmail(EmailConfirmRequest request) throws MessagingException, IOException {
        try{
            if (request.getEmail().isEmpty()){
                request.setEmail(userRepository.findByUsername(request.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getEmail());
            }
            emailService.sendEmail(request.getEmail(), request.getUsername());
        }catch (MessagingException | IOException e) {
            throw new RuntimeException("Error sending confirmation email: " + e.getMessage(), e);
        }
    }

    public RegisterResponse register(RegisterRequest request) throws IOException, MessagingException {
        if (userRepository.existsByUsername(request.getUsername())){
            System.out.println("Username already exists: " + request.getUsername());
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Password and confirm password do not match");
        }
        if (!otpUtils.verifyOtp(request.getEmail(), request.getOtpCode())) {
            throw new RuntimeException("Invalid OTP code");
        }


        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setRole(Role.ROLE_USER);

        Image avatar = new Image();
        avatar.setId(1L);
        newUser.setAvatar(avatar); // Assuming avatar is handled separately
        
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

                CustomUserDetails userDetails = new CustomUserDetails(user.get());
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println(userDetails.getAuthorities());
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
                CustomUserDetails userDetails = new CustomUserDetails(user.get());
                return jwtUtil.generateAccessToken(userDetails);
            }else {
                throw new RuntimeException("Invalid refresh token");
            }
        }else {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
