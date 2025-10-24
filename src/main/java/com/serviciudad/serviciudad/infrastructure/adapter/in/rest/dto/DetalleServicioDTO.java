package com.serviciudad.serviciudad.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

public class DetalleServicioDTO {
    private String periodo;
    private String consumo;
    private BigDecimal valorPagar;

    public DetalleServicioDTO() {}

    public DetalleServicioDTO(String periodo, String consumo, BigDecimal valorPagar) {
        this.periodo = periodo;
        this.consumo = consumo;
        this.valorPagar = valorPagar;
    }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getConsumo() { return consumo; }
    public void setConsumo(String consumo) { this.consumo = consumo; }

    public BigDecimal getValorPagar() { return valorPagar; }
    public void setValorPagar(BigDecimal valorPagar) { this.valorPagar = valorPagar; }
}

