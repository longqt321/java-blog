package org.example.javablog.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpUtils {
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        storeOtp(email, otp);
        return otp;
    }
    private void storeOtp(String email, String otp) {
        otpStorage.put(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        if (otp.equals(otpStorage.get(email))) {
            removeOtp(email);
            return true;
        }
        return false;
    }

    private void removeOtp(String email) {
        otpStorage.remove(email);
    }
}
