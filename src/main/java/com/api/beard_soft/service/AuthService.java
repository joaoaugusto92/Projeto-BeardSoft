package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.dto.user.Login.LoginRequestDto;
import com.api.beard_soft.dto.user.UserCreateDto;
import com.api.beard_soft.infra.security.TokenService;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthService (UserRepository userRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, TokenService tokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public UserEntity registerUser(UserCreateDto data){
        //verificação de usuário;
        if (this.userRepository.findByEmail(data.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // Criptografa a senha
        String encryptedPassword = passwordEncoder.encode(data.password());

        // Cria e salva o novo usuário
        UserEntity newUser = new UserEntity(data.email(), encryptedPassword, UserRole.CLIENT);
        return this.userRepository.save(newUser);
    }

    public String loginUser(LoginRequestDto data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        return tokenService.generateToken((UserEntity) auth.getPrincipal());
    }

    public UserEntity registerAdmin(UserCreateDto data){
        //verificação de usuário;
        if (this.userRepository.findByEmail(data.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // Criptografa a senha
        String encryptedPassword = passwordEncoder.encode(data.password());

        // Cria e salva o novo usuário
        UserEntity newUser = new UserEntity(data.email(), encryptedPassword, UserRole.ADMIN);
        return this.userRepository.save(newUser);
    }
}
