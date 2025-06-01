package org.example.javablog.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.javablog.security.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private OtpUtils otpUtils;

    public void sendEmail(String to, String subject, String body) {
         SimpleMailMessage message = new SimpleMailMessage();
         message.setTo(to);
         message.setSubject(subject);
         message.setText(body);
         mailSender.send(message);
    }
    public void sendRegistrationConfirmEmail(String to, String username) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");


        String htmlTemplate = loadEmailTemplate();
        String htmlContent = htmlTemplate
                .replace("{{USERNAME}}", username)
                .replace("{{OTP_CODE}}", otpUtils.generateOtp(to));

        helper.setTo(to);
        helper.setSubject("Registration Confirmation");
        helper.setText(htmlContent, true); // true => HTML

        mailSender.send(message);
    }

    private String loadEmailTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/" + "registration_confirm.html");
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
}
