package com.serviciudad.serviciudad.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacturaAcueductoRepositoryJpa extends JpaRepository<FacturaAcueductoEntity, Long> {
    List<FacturaAcueductoEntity> findByIdCliente(String idCliente);
}
