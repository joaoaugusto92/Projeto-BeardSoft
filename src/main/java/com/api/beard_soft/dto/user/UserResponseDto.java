package com.api.beard_soft.dto.user;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        String phoneNumber
) {
}
