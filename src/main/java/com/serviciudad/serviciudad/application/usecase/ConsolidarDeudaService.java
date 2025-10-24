package com.serviciudad.serviciudad.application.usecase;

import com.serviciudad.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.serviciudad.domain.model.FacturaEnergia;
import com.serviciudad.serviciudad.domain.port.out.FacturasAcueductoPort;
import com.serviciudad.serviciudad.domain.port.out.FacturasEnergiaPort;
import com.serviciudad.serviciudad.infrastructure.adapter.in.rest.dto.DeudaConsolidadaDTO;
import com.serviciudad.serviciudad.infrastructure.adapter.in.rest.dto.DetalleServicioDTO;
import com.serviciudad.serviciudad.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ConsolidarDeudaService implements ObtenerDeudaUseCase {

    private final FacturasEnergiaPort energiaPort;
    private final FacturasAcueductoPort acueductoPort;

    public ConsolidarDeudaService(FacturasEnergiaPort energiaPort,
                                  FacturasAcueductoPort acueductoPort) {
        this.energiaPort = energiaPort;
        this.acueductoPort = acueductoPort;
    }

    @Override
    public DeudaConsolidadaDTO obtenerDeuda(String clienteId) {
        // 1) obtener facturas de energia desde el adaptador (archivo)
        List<FacturaEnergia> facturasEnergia = energiaPort.obtenerFacturas();
        Optional<FacturaEnergia> facturaEnergiaOpt = facturasEnergia.stream()
                .filter(f -> f.getIdCliente() != null && f.getIdCliente().equals(clienteId))
                .findFirst();

        // 2) obtener facturas acueducto desde la base de datos
        List<FacturaAcueducto> facturasAcueducto = acueductoPort.findByCliente(clienteId);

        if (facturaEnergiaOpt.isEmpty() && (facturasAcueducto == null || facturasAcueducto.isEmpty())) {
            throw new NotFoundException("Cliente no encontrado en sistemas: " + clienteId);
        }

        DetalleServicioDTO detalleEnergia = null;
        if (facturaEnergiaOpt.isPresent()) {
            FacturaEnergia fe = facturaEnergiaOpt.get();
            detalleEnergia = new DetalleServicioDTO(fe.getPeriodo(), fe.getConsumoKwh() + " kWh", fe.getValorPagar());
        }

        DetalleServicioDTO detalleAcueducto = null;
        if (facturasAcueducto != null && !facturasAcueducto.isEmpty()) {
            FacturaAcueducto fa = facturasAcueducto.stream()
                    .max(Comparator.comparing(FacturaAcueducto::getPeriodo))
                    .orElse(facturasAcueducto.get(0));
            detalleAcueducto = new DetalleServicioDTO(fa.getPeriodo(), fa.getConsumoM3() + " m³", fa.getValorPagar());
        }

        BigDecimal total = BigDecimal.ZERO;
        if (detalleEnergia != null && detalleEnergia.getValorPagar() != null) total = total.add(detalleEnergia.getValorPagar());
        if (detalleAcueducto != null && detalleAcueducto.getValorPagar() != null) total = total.add(detalleAcueducto.getValorPagar());

        return new DeudaConsolidadaDTO.Builder()
                .clienteId(clienteId)
                .nombreCliente("Emmanuel Solarte")
                .fechaConsulta(Instant.now())
                .energia(detalleEnergia)
                .acueducto(detalleAcueducto)
                .totalAPagar(total)
                .build();
    }
}
