package com.serviciudad.serviciudad.domain.port.out;

import com.serviciudad.serviciudad.domain.model.FacturaAcueducto;

import java.util.List;

public interface FacturasAcueductoPort {
    List<FacturaAcueducto> findByCliente(String clienteId);
}
