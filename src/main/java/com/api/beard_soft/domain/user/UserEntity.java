package com.api.beard_soft.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity(@NotBlank(message = "O email é obrigatorio!") @Email(message = "Email inválido!") String email, String password, UserRole Role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
