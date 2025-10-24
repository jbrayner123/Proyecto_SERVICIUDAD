package com.serviciudad.serviciudad.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class DeudaConsolidadaDTO {
    private String clienteId;
    private String nombreCliente;
    private Instant fechaConsulta;
    private DetalleServicioDTO energia;
    private DetalleServicioDTO acueducto;
    private BigDecimal totalAPagar;

    private DeudaConsolidadaDTO(Builder b) {
        this.clienteId = b.clienteId;
        this.nombreCliente = b.nombreCliente;
        this.fechaConsulta = b.fechaConsulta;
        this.energia = b.energia;
        this.acueducto = b.acueducto;
        this.totalAPagar = b.totalAPagar;
    }

    public String getClienteId() { return clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public Instant getFechaConsulta() { return fechaConsulta; }
    public DetalleServicioDTO getEnergia() { return energia; }
    public DetalleServicioDTO getAcueducto() { return acueducto; }
    public BigDecimal getTotalAPagar() { return totalAPagar; }

    // Builder pattern
    public static class Builder {
        private String clienteId;
        private String nombreCliente;
        private Instant fechaConsulta;
        private DetalleServicioDTO energia;
        private DetalleServicioDTO acueducto;
        private BigDecimal totalAPagar;

        public Builder clienteId(String clienteId) { this.clienteId = clienteId; return this; }
        public Builder nombreCliente(String nombre) { this.nombreCliente = nombre; return this; }
        public Builder fechaConsulta(Instant fecha) { this.fechaConsulta = fecha; return this; }
        public Builder energia(DetalleServicioDTO d) { this.energia = d; return this; }
        public Builder acueducto(DetalleServicioDTO d) { this.acueducto = d; return this; }
        public Builder totalAPagar(BigDecimal total) { this.totalAPagar = total; return this; }
        public DeudaConsolidadaDTO build() { return new DeudaConsolidadaDTO(this); }
    }
}

