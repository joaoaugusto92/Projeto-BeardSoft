package com.api.beard_soft.dto.user.clients;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ClientRequestDto(
        @NotBlank(message = "O nome é obrigatório!")
        String name,
        
        @NotBlank(message = "O email é obrigatório!")
        @Email(message = "Email inválido!")
        String email,
        
        String phoneNumber,
        
        String password,
        
        LocalDate birthDate
) {
}
