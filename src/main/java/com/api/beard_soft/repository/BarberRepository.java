package com.api.beard_soft.repository;

import com.api.beard_soft.domain.user.Baber.BarberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberRepository extends JpaRepository<BarberEntity, Long> {
}
