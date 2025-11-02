package com.api.beard_soft.repository;

import com.api.beard_soft.domain.user.Client.ClientEntity;
import com.api.beard_soft.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository <ClientEntity, Long> {
    Optional<ClientEntity> findByUser(UserEntity user);
}
