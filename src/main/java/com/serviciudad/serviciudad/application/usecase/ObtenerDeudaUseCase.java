package com.serviciudad.serviciudad.application.usecase;

import com.serviciudad.serviciudad.infrastructure.adapter.in.rest.dto.DeudaConsolidadaDTO;

public interface ObtenerDeudaUseCase {
    DeudaConsolidadaDTO obtenerDeuda(String clienteId);
}
