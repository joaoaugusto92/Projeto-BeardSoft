package com.api.beard_soft;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BeardSoftApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeardSoftApplication.class, args);
	}

	@Bean
	CommandLineRunner initDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String adminEmail = "admin@admin.com";
			if (userRepository.findByEmail(adminEmail).isEmpty()) {
				UserEntity admin = new UserEntity();
				admin.setName("Administrador");
				admin.setEmail(adminEmail);
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(UserRole.ADMIN);
				admin.setPhoneNumber("00000000000");
				userRepository.save(admin);
				System.out.println(">>> Default admin user created: " + adminEmail + " / admin123");
			} else {
				System.out.println(">>> Default admin user already exists: " + adminEmail);
			}
		};
	}
}
