package com.api.beard_soft.domain.user.Client;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.domain.user.appointments.AppointmentsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clients")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    // Para envio de promoções de aniversário
    private LocalDate birthDate;
    // Para rastrear a fidelidade ou inatividade.
    private LocalDateTime lastVisitDate;
    // Relacionamento 1:N (Um Cliente tem MUITOS Agendamentos)
    // 'mappedBy' aponta para o campo 'client' dentro da AppointmentEntity.
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<AppointmentsEntity> appointments;
}
