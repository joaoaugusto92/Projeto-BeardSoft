package com.api.beard_soft.dto.user.clients;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponseDto(
        Long id,
        Long userId,
        String name,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        LocalDateTime lastVisitDate
) {
}
