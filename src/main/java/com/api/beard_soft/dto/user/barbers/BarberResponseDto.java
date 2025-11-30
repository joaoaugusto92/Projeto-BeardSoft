package com.api.beard_soft.dto.user.barbers;

import java.math.BigDecimal;

public record BarberResponseDto(
        Long id,
        Long userId,
        String name,
        String email,
        String phoneNumber,
        String profileImgURL,
        BigDecimal defaultCommissionPercentage,
        Boolean isActive
) {
}
