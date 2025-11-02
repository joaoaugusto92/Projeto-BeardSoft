package com.api.beard_soft.dto.user.Appointments;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentsRescheduleRequestDto(
        @NotNull @FutureOrPresent LocalDate newDate,
        @NotNull LocalTime newTime
) {
}
