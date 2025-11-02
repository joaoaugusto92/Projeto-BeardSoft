package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.Baber.BarberEntity;
import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.repository.BarberRepository;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InitializerService {
    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public InitializerService(UserRepository userRepository, BarberRepository barberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.barberRepository = barberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createBarberUser(String email, String name, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // 1. Cria e Salva o UserEntity
        UserEntity barberUser = new UserEntity();
        barberUser.setName(name);
        barberUser.setEmail(email);
        barberUser.setPassword(passwordEncoder.encode(rawPassword));
        barberUser.setRole(UserRole.ADMIN); // Ou UserRole.BARBER
        UserEntity savedUser = userRepository.save(barberUser);

        // 2. Cria e Salva o BarberEntity associado
        BarberEntity barber = new BarberEntity();
        barber.setUser(savedUser);
        barber.setName(name);
        barber.setIsActive(true);
        barber.setDefaultCommissionPercentage(new BigDecimal("0.30")); // Exemplo de valor

        barberRepository.save(barber);
    }
}
