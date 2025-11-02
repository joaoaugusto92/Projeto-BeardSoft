package com.api.beard_soft.domain.user.Baber;

import com.api.beard_soft.domain.user.UserEntity;
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
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "profile_img_url")
    private String profileImgURL;
    @Column(name = "default_commission_percentage")
    private BigDecimal defaultCommissionPercentage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity user;



    // Relacionamentos: O barbeiro tem vários agendamentos
    // FetchType.LAZY para não carregar todos os agendamentos sempre
    @OneToMany(mappedBy = "barber", fetch = FetchType.LAZY)
    private Set<AppointmentsEntity> appointments;
}
