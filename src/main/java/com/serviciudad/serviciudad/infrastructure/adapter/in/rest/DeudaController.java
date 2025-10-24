package com.serviciudad.serviciudad.infrastructure.adapter.in.rest;

import com.serviciudad.serviciudad.application.usecase.ObtenerDeudaUseCase;
import com.serviciudad.serviciudad.infrastructure.adapter.in.rest.dto.DeudaConsolidadaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class DeudaController {

    private final ObtenerDeudaUseCase obtenerDeudaUseCase;

    public DeudaController(ObtenerDeudaUseCase obtenerDeudaUseCase) {
        this.obtenerDeudaUseCase = obtenerDeudaUseCase;
    }

    @GetMapping("/{clienteId}/deuda-consolidada")
    public ResponseEntity<DeudaConsolidadaDTO> getDeuda(@PathVariable String clienteId) {
        DeudaConsolidadaDTO dto = obtenerDeudaUseCase.obtenerDeuda(clienteId);
        return ResponseEntity.ok(dto);
    }
}
