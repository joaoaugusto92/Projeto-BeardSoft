package com.api.beard_soft.dto.user.services;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServiceRequestDto(
        Long id,
        @NotBlank(message = "O nome é obrigatório!")
        @Size(max = 100, message = "Máximo de caracteres é 100!")
        @Column(unique = true, nullable = false)
        String name,

        @Size(max = 200, min = 50)
        String description,

        @DecimalMin(value = "0.00", message = "Valor não pode ser menor que zero!")
        BigDecimal value,

        String imageURL,

        @Min(5)
        @Max(180)
        Integer durationInMinutes,

        @NotBlank(message = "A categoria é obrigatório!")
        String category,

        Boolean isActive
) {
}
