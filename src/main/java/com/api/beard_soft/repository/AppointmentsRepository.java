package com.api.beard_soft.repository;

import com.api.beard_soft.domain.user.appointments.AppointmentsEntity;
import com.api.beard_soft.domain.user.appointments.AppointmentsStatus;
import com.api.beard_soft.domain.user.service.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentsRepository extends JpaRepository<AppointmentsEntity, Long> {
    long countByServiceAndStartTimeAfterAndStatusIsNotIn(
            ServiceEntity service, // Agora recebe o objeto Services
            LocalDateTime currentDateTime,
            List<AppointmentsStatus> excludedStatus
    );

    Long countByService(ServiceEntity service);
}
