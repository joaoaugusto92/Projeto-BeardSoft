package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.dto.user.UserResponseDto;
import com.api.beard_soft.dto.user.UserUpdateDto;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto findByEmail(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado!"));

        return mapUserToUserResponseDto(user);
    }

    public UserResponseDto mapUserToUserResponseDto(UserEntity user){
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );

    }

    public UserResponseDto updateUser(String email, UserUpdateDto userUpdateDTO){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!"));

        if(userUpdateDTO.name() != null && !userUpdateDTO.name().trim().isEmpty()){
            user.setName(userUpdateDTO.name());
        }

        if(userUpdateDTO.phoneNumber() != null){
            user.setPhoneNumber(userUpdateDTO.phoneNumber());
        }

        if (userUpdateDTO.password() != null && !userUpdateDTO.password().trim().isEmpty()){
            String hashedPassword = passwordEncoder.encode(userUpdateDTO.password());
            user.setPassword(hashedPassword);
        }

        UserEntity updatedUser = userRepository.save(user);

        return mapUserToUserResponseDto(updatedUser);
    }


}
