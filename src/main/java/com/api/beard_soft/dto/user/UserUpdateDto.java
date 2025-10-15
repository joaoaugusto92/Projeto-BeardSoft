package com.api.beard_soft.dto.user;

public record UserUpdateDto(
        String name,
        String phoneNumber,
        String password
) {
}
