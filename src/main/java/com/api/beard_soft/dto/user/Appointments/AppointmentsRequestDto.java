package com.api.beard_soft.dto.user.Appointments;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentsRequestDto(
        @NotNull(message = "O Id do serviço é obrigatório") Long serviceId,
        @NotNull(message = "O Id do cliente é obrigatório") Long clientId,
        @NotNull(message = "O Id do barbeiro é obrigatório") Long barberId,
        @NotNull @FutureOrPresent LocalDate date,
        @NotNull LocalTime time
) {
}
