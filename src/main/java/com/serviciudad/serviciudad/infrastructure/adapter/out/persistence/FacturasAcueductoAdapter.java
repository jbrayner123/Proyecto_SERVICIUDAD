package com.serviciudad.serviciudad.infrastructure.adapter.out.persistence;

import com.serviciudad.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.serviciudad.domain.port.out.FacturasAcueductoPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FacturasAcueductoAdapter implements FacturasAcueductoPort {

    private final FacturaAcueductoRepositoryJpa repository;

    public FacturasAcueductoAdapter(FacturaAcueductoRepositoryJpa repository) {
        this.repository = repository;
    }

    @Override
    public List<FacturaAcueducto> findByCliente(String clienteId) {
        List<FacturaAcueductoEntity> entities = repository.findByIdCliente(clienteId);
        return entities.stream()
                .map(e -> new FacturaAcueducto(e.getId(), e.getIdCliente(), e.getPeriodo(), 
                        e.getConsumoM3() == null ? 0 : e.getConsumoM3(), 
                        e.getValorPagar() == null ? java.math.BigDecimal.ZERO : e.getValorPagar()))
                .collect(Collectors.toList());
    }
}
