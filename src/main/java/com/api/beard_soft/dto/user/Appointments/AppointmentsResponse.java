package com.api.beard_soft.dto.user.Appointments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentsResponse(
        Long id,
        String clientName,
        String clientEmail,
        String serviceName,
        String serviceDuration,
        BigDecimal serviceValue,
        LocalDateTime startTime, // Útil para combinar data e hora
        String status
) {
}
