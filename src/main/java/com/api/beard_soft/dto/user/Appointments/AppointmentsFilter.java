package com.api.beard_soft.dto.user.Appointments;

import java.time.LocalDate;

public record AppointmentsFilter(
        LocalDate startDate, // Não precisa ser @NotNull se for opcional
        LocalDate endDate,
        Long clientId
) {
}
