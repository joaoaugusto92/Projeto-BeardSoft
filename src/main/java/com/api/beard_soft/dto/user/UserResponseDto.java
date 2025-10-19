package com.api.beard_soft.dto.user;

import java.util.List;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        String phoneNumber
) {
}
