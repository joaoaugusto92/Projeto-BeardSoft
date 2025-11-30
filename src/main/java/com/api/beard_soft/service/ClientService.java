package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.Client.ClientEntity;
import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.dto.user.clients.ClientRequestDto;
import com.api.beard_soft.dto.user.clients.ClientResponseDto;
import com.api.beard_soft.repository.ClientRepository;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public ClientService(ClientRepository clientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = true)
    public List<ClientResponseDto> findAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ClientResponseDto findClientById(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente com ID " + id + " não encontrado!"
                ));
        return convertToDto(client);
    }
    
    @Transactional
    public ClientResponseDto createClient(ClientRequestDto requestDto) {
        // Check if email already exists
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado!");
        }
        
        // Create user entity
        UserEntity user = new UserEntity();
        user.setName(requestDto.name());
        user.setEmail(requestDto.email());
        user.setPhoneNumber(requestDto.phoneNumber());
        if (requestDto.password() != null && !requestDto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
        } else {
            // Generate a default password if not provided
            user.setPassword(passwordEncoder.encode("temp123456"));
        }
        user.setRole(UserRole.CLIENT);
        
        UserEntity savedUser = userRepository.save(user);
        
        // Create client entity
        ClientEntity client = new ClientEntity();
        client.setUser(savedUser);
        client.setBirthDate(requestDto.birthDate());
        
        ClientEntity savedClient = clientRepository.save(client);
        
        return convertToDto(savedClient);
    }
    
    @Transactional
    public ClientResponseDto updateClient(Long id, ClientRequestDto requestDto) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente com ID " + id + " não encontrado!"
                ));
        
        UserEntity user = client.getUser();
        
        if (requestDto.name() != null && !requestDto.name().isEmpty()) {
            user.setName(requestDto.name());
        }
        if (requestDto.email() != null && !requestDto.email().isEmpty()) {
            // Check if email is being changed and if new email already exists
            if (!user.getEmail().equals(requestDto.email()) && 
                userRepository.findByEmail(requestDto.email()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado!");
            }
            user.setEmail(requestDto.email());
        }
        if (requestDto.phoneNumber() != null) {
            user.setPhoneNumber(requestDto.phoneNumber());
        }
        if (requestDto.password() != null && !requestDto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
        }
        if (requestDto.birthDate() != null) {
            client.setBirthDate(requestDto.birthDate());
        }
        
        userRepository.save(user);
        ClientEntity savedClient = clientRepository.save(client);
        
        return convertToDto(savedClient);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente com ID " + id + " não encontrado!"
                ));
        
        clientRepository.delete(client);
        userRepository.delete(client.getUser());
    }
    
    private ClientResponseDto convertToDto(ClientEntity client) {
        UserEntity user = client.getUser();
        return new ClientResponseDto(
                client.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                client.getBirthDate(),
                client.getLastVisitDate()
        );
    }
}
