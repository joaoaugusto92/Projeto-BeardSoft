package com.api.beard_soft.infra.security.config;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {
    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@beardsoft.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String encodedPassword = passwordEncoder.encode("admin123");

                UserEntity admin = new UserEntity();
                admin.setName("Administrador");
                admin.setEmail(adminEmail);
                admin.setPassword(encodedPassword);
                admin.setRole(UserRole.ADMIN);

                userRepository.save(admin);

                System.out.println("✅ Usuário admin criado com sucesso!");
                System.out.println("Email: " + adminEmail);
                System.out.println("Senha: admin123");
            } else {
                System.out.println("ℹ️ Usuário admin já existe, nenhum novo foi criado.");
            }
        };
    }
}
