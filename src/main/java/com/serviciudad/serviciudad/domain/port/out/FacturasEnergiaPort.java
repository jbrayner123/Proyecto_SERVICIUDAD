package com.serviciudad.serviciudad.domain.port.out;


import java.util.List;

import com.serviciudad.serviciudad.domain.model.FacturaEnergia;

public interface FacturasEnergiaPort {
    List<FacturaEnergia> obtenerFacturas();
}

