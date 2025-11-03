package com.api.beard_soft.repository;

import com.api.beard_soft.domain.user.Baber.BarberEntity;
import com.api.beard_soft.domain.user.Client.ClientEntity;
import com.api.beard_soft.domain.user.appointments.AppointmentsEntity;
import com.api.beard_soft.domain.user.appointments.AppointmentsStatus;
import com.api.beard_soft.domain.user.service.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentsRepository extends JpaRepository<AppointmentsEntity, Long> {
    long countByServiceAndStartTimeAfterAndStatusIsNotIn(
            ServiceEntity service, // Agora recebe o objeto Services
            LocalDateTime currentDateTime,
            List<AppointmentsStatus> excludedStatus
    );

    @Query("SELECT a FROM AppointmentsEntity a " +
            "JOIN FETCH a.client c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH a.barber b " +             // NOVO: Se estiver acessando o Barbeiro
            "JOIN FETCH a.service s " +            // NOVO: Se estiver acessando o Serviço
            "WHERE a.client = :client")
    List<AppointmentsEntity> findByClientWithEagerData(ClientEntity client);

    Long countByService(ServiceEntity service);

    @Query("""
        SELECT a FROM AppointmentsEntity a
        WHERE a.barber = :barber
        AND a.status IN :activeStatuses
        AND (
            (a.startTime < :newEndTime AND a.endTime > :newStartTime)
        )
    """)
    Optional<AppointmentsEntity> findFirstConflictForBarber(
            @Param("newStartTime") LocalDateTime newStartTime,
            @Param("newEndTime") LocalDateTime newEndTime,
            @Param("barber") BarberEntity barber, // NOVO: Filtro por Barbeiro
            @Param("activeStatuses") List<AppointmentsStatus> activeStatuses);


    @Query("SELECT a FROM AppointmentsEntity a " +
            "JOIN FETCH a.client c JOIN FETCH c.user u " +
            "JOIN FETCH a.barber b " +  // Adicionando Barbeiro para prevenir L.I.E. se necessário
            "JOIN FETCH a.service s " + // Adicionando Serviço para prevenir L.I.E. se necessário
            "WHERE a.startTime BETWEEN :start AND :end " + // <-- CORREÇÃO CRÍTICA AQUI!
            "ORDER BY a.startTime")
    List<AppointmentsEntity> findAppointmentsWithDetailsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT a FROM AppointmentsEntity a
        WHERE a.barber = :barber
        AND a.status IN :activeStatuses
        AND a.id != :excludedAppointmentId
        AND (
            (:startTime < a.endTime AND :endTime > a.startTime)
        )
        ORDER BY a.startTime
    """)
    Optional<AppointmentsEntity> findConflictForBarberExcludingId(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("barber") BarberEntity barber,
            @Param("activeStatuses") List<AppointmentsStatus> activeStatuses,
            @Param("excludedAppointmentId") Long excludedAppointmentId // <--- NOVO PARÂMETRO
    );
}


