package com.api.beard_soft.dto.user.Login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "O email é obrigatorio!")
        @Email(message = "Email inválido!")
        String email,

        @NotBlank(message = "A senha é obrigatorio!")
        @Size(min = 8, max = 16, message = "A senha deve ter entre 8 e 16 caracteres")
        String password
        ) {
}
