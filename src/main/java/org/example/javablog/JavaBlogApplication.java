package org.example.javablog;

import org.example.javablog.constant.Role;
import org.example.javablog.model.User;
import org.example.javablog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class JavaBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaBlogApplication.class, args);
	}
	@Value("${admin.username}")
	private String adminUsername;
	@Value("${admin.password}")
	private String adminPassword;

	@Bean
	CommandLineRunner initAdmin(UserRepository userRepository,
								PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByUsername(adminUsername)) {
				User user = new User();
				user.setUsername(adminUsername);
				user.setPassword(passwordEncoder.encode(adminPassword));
				user.setRole(Role.ROLE_ADMIN);
				user.setFirstName("Admin");
				user.setLastName("Admin");
				userRepository.save(user);
			}
		};
	}
}
