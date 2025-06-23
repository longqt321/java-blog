package org.example.javablog.util;

import org.example.javablog.dto.UserDTO;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

public class SessionUtils {
    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Objects.requireNonNull(userDetails.getId(), "User details cannot be null");
    }
}
