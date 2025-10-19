package com.api.beard_soft.domain.user.service;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 100, message = "Máximo de caracteres é 100!")
    @Column(unique = true, nullable = false)
    private String name;
    @Size(max = 200, min = 50)
    private String description;
    @DecimalMin(value = "0.00", message = "Valor não pode ser menor que zero!")
    private BigDecimal value;
    private String imageUrl; // url da imagem do serviço
    @Min(5)
    @Max(180)
    private Integer durationInMinutes; /* tempo de duração do serviço;
    será utilizado para calcular o tempo que será alocado ao agendar este serviço; */
    private String category;
    private Boolean isActive;
}
