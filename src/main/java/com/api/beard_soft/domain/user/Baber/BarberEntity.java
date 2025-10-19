package com.api.beard_soft.domain.user.Baber;

import com.api.beard_soft.domain.user.appointments.AppointmentsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "barbers")
public class BarberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean isActive;
    private String profileImgURL;
    private BigDecimal defaultCommissionPercentage;

    // Relacionamentos: O barbeiro tem vários agendamentos
    // FetchType.LAZY para não carregar todos os agendamentos sempre
    @OneToMany(mappedBy = "barber", fetch = FetchType.LAZY)
    private Set<AppointmentsEntity> appointments;
}
