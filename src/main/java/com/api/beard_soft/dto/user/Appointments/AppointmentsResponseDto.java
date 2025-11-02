package com.api.beard_soft.dto.user.Appointments;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentsResponseDto(
        Long id,
        // Dados do Cliente
        Long clientId,
        String clientName,
        String clientEmail,
        // Dados do Barbeiro
        Long barberId,
        String barberName,
        // Dados do Serviço
        Long serviceId,
        String serviceName,
        Integer durationMinutes,
        String serviceDurationDisplay, // Duração formatada (Ex: "1h 30m")
        BigDecimal serviceValue,       // Valor em BigDecimal (para cálculos)
        String serviceValueDisplay,    // Valor formatado (Ex: "R$ 50,00")

        // Dados do Agendamento
        LocalDateTime startTime,
        LocalDateTime endTime,        // Adicionado para clareza
        String status
) {
}
