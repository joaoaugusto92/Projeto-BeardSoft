package com.api.beard_soft.dto.user.barbers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record BarberRequestDto(
        @NotBlank(message = "O nome é obrigatório!")
        String name,
        
        @NotBlank(message = "O email é obrigatório!")
        @Email(message = "Email inválido!")
        String email,
        
        String phoneNumber,
        
        String password,
        
        String profileImgURL,
        
        BigDecimal defaultCommissionPercentage,
        
        Boolean isActive
) {
}
