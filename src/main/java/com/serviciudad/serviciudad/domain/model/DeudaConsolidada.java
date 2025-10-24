package com.serviciudad.serviciudad.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class DeudaConsolidada {
    private String clienteId;
    private String nombreCliente;
    private FacturaEnergia facturaEnergia; // nullable
    private FacturaAcueducto facturaAcueducto; // nullable
    private Instant fechaConsulta;

    public DeudaConsolidada(String clienteId, String nombreCliente,
                            FacturaEnergia facturaEnergia, FacturaAcueducto facturaAcueducto, Instant fechaConsulta) {
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.facturaEnergia = facturaEnergia;
        this.facturaAcueducto = facturaAcueducto;
        this.fechaConsulta = fechaConsulta;
    }

    public String getClienteId() { return clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public FacturaEnergia getFacturaEnergia() { return facturaEnergia; }
    public FacturaAcueducto getFacturaAcueducto() { return facturaAcueducto; }
    public Instant getFechaConsulta() { return fechaConsulta; }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (facturaEnergia != null && facturaEnergia.getValorPagar() != null) {
            total = total.add(facturaEnergia.getValorPagar());
        }
        if (facturaAcueducto != null && facturaAcueducto.getValorPagar() != null) {
            total = total.add(facturaAcueducto.getValorPagar());
        }
        return total;
    }
}
