package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.Baber.BarberEntity;
import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.UserRole;
import com.api.beard_soft.dto.user.barbers.BarberRequestDto;
import com.api.beard_soft.dto.user.barbers.BarberResponseDto;
import com.api.beard_soft.repository.BarberRepository;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarberService {
    
    private final BarberRepository barberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public BarberService(BarberRepository barberRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.barberRepository = barberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = true)
    public List<BarberResponseDto> findAllBarbers() {
        return barberRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BarberResponseDto findBarberById(Long id) {
        BarberEntity barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barbeiro com ID " + id + " não encontrado!"
                ));
        return convertToDto(barber);
    }
    
    @Transactional
    public BarberResponseDto createBarber(BarberRequestDto requestDto) {
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
        user.setRole(UserRole.ADMIN); // Barbers are typically admins
        
        UserEntity savedUser = userRepository.save(user);
        
        // Create barber entity
        BarberEntity barber = new BarberEntity();
        barber.setUser(savedUser);
        barber.setName(requestDto.name());
        barber.setProfileImgURL(requestDto.profileImgURL());
        barber.setDefaultCommissionPercentage(requestDto.defaultCommissionPercentage());
        barber.setIsActive(requestDto.isActive() != null ? requestDto.isActive() : true);
        
        BarberEntity savedBarber = barberRepository.save(barber);
        
        return convertToDto(savedBarber);
    }
    
    @Transactional
    public BarberResponseDto updateBarber(Long id, BarberRequestDto requestDto) {
        BarberEntity barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barbeiro com ID " + id + " não encontrado!"
                ));
        
        UserEntity user = barber.getUser();
        
        if (requestDto.name() != null && !requestDto.name().isEmpty()) {
            user.setName(requestDto.name());
            barber.setName(requestDto.name());
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
        if (requestDto.profileImgURL() != null) {
            barber.setProfileImgURL(requestDto.profileImgURL());
        }
        if (requestDto.defaultCommissionPercentage() != null) {
            barber.setDefaultCommissionPercentage(requestDto.defaultCommissionPercentage());
        }
        if (requestDto.isActive() != null) {
            barber.setIsActive(requestDto.isActive());
        }
        
        userRepository.save(user);
        BarberEntity savedBarber = barberRepository.save(barber);
        
        return convertToDto(savedBarber);
    }
    
    @Transactional
    public BarberResponseDto deactivateBarber(Long id) {
        BarberEntity barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barbeiro com ID " + id + " não encontrado!"
                ));
        
        barber.setIsActive(false);
        BarberEntity savedBarber = barberRepository.save(barber);
        
        return convertToDto(savedBarber);
    }
    
    @Transactional
    public BarberResponseDto activateBarber(Long id) {
        BarberEntity barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barbeiro com ID " + id + " não encontrado!"
                ));
        
        barber.setIsActive(true);
        BarberEntity savedBarber = barberRepository.save(barber);
        
        return convertToDto(savedBarber);
    }
    
    @Transactional
    public void deleteBarber(Long id) {
        BarberEntity barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barbeiro com ID " + id + " não encontrado!"
                ));
        
        barberRepository.delete(barber);
        userRepository.delete(barber.getUser());
    }
    
    private BarberResponseDto convertToDto(BarberEntity barber) {
        UserEntity user = barber.getUser();
        return new BarberResponseDto(
                barber.getId(),
                user.getId(),
                barber.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                barber.getProfileImgURL(),
                barber.getDefaultCommissionPercentage(),
                barber.getIsActive()
        );
    }
}
