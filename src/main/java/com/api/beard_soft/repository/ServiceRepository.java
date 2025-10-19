package com.api.beard_soft.repository;

import com.api.beard_soft.domain.user.service.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.Provider;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<ServiceEntity> findByNameAndIdNot(String name, Long id);
}
