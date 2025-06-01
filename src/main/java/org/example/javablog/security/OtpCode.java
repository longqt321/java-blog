package org.example.javablog.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OtpCode {
    private String code;
    private Long expirationTime;
}
