package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.Client.ClientEntity;
import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.dto.user.Login.LoginRequestDto;
import com.api.beard_soft.dto.user.UserCreateDto;
import com.api.beard_soft.infra.security.TokenService;
import com.api.beard_soft.repository.ClientRepository;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService (UserRepository userRepository,ClientRepository clientRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, TokenService tokenService){
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
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
        UserEntity newUser = new UserEntity();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPhoneNumber(data.phoneNumber());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.CLIENT);
        UserEntity savedUser = this.userRepository.save(newUser);

        ClientEntity newClient = new ClientEntity();
        newClient.setUser(savedUser);

        this.clientRepository.save(newClient);

        return savedUser;
    }

    public String loginUser(LoginRequestDto data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        // 1. Autentica e recebe o objeto de segurança do Spring
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        // 2. Obtém o objeto principal (que é um UserDetails)
        UserDetails userDetails = (UserDetails) auth.getPrincipal(); // Caste para a INTERFACE UserDetails

        // 3. Usa o username (email) para buscar a ENTIDADE JPA no repositório
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Erro interno: Usuário autenticado não encontrado."));

        // 4. Gera o token passando a ENTIDADE JPA correta
        return tokenService.generateToken(userEntity);
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
