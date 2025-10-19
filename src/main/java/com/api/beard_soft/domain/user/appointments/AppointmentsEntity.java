package com.api.beard_soft.domain.user.appointments;

import com.api.beard_soft.domain.user.Baber.BarberEntity;
import com.api.beard_soft.domain.user.Client.ClientEntity;
import com.api.beard_soft.domain.user.service.ServiceEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointments")
public class AppointmentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private BarberEntity barber;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;
    @NotNull
    private LocalDateTime startTime; //Horário que será selecionado pelo cliente.
    @NotNull
    private LocalDateTime endTime; //startTime + durantionInMinutes, para deixar agendado o tempo necessário.
    //Ex: O cliente agenda o horário de 13h e escolheu um serviço que dura 60min, dessa forma o horário de 13h às 14h estará indisponível.
    @NotNull
    private Integer durationInMinutes; //copiada do ServiceEntity

    @Enumerated(EnumType.STRING) //Armazena string no Banco de dados
    private AppointmentsStatus status;

}
