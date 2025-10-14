package com.api.beard_soft.dto.user;

public record UserUpdateDto(
        String name,
        String email,
        String password
) {
}
