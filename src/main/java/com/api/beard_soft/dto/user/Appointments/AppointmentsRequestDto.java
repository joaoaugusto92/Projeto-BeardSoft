package com.api.beard_soft.dto.user.Appointments;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentsRequestDto(
        @NotNull(message = "O Id do serviço é obrigatório") Long serviceId,
        @NotNull(message = "O Id do cliente é obrigatório") Long clientId,
        @NotNull(message = "O Id do barbeiro é obrigatório") Long barberId,
        @NotNull(message = "A data é obrigatória") 
        @JsonFormat(pattern = "yyyy-MM-dd") 
        LocalDate date,
        @NotNull(message = "A hora é obrigatória") 
        @JsonFormat(pattern = "HH:mm:ss") 
        LocalTime time
) {
    @Override
    public String toString() {
        return "AppointmentsRequestDto{" +
                "serviceId=" + serviceId +
                ", clientId=" + clientId +
                ", barberId=" + barberId +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
