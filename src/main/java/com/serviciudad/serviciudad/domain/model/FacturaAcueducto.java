package com.serviciudad.serviciudad.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class FacturaAcueducto {
    private Long id;
    private String idCliente;
    private String periodo;
    private int consumoM3;
    private BigDecimal valorPagar;

    public FacturaAcueducto(Long id, String idCliente, String periodo, int consumoM3, BigDecimal valorPagar) {
        this.id = id;
        this.idCliente = idCliente;
        this.periodo = periodo;
        this.consumoM3 = consumoM3;
        this.valorPagar = valorPagar;
    }

    public Long getId() { return id; }
    public String getIdCliente() { return idCliente; }
    public String getPeriodo() { return periodo; }
    public int getConsumoM3() { return consumoM3; }
    public BigDecimal getValorPagar() { return valorPagar; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacturaAcueducto)) return false;
        FacturaAcueducto that = (FacturaAcueducto) o;
        return consumoM3 == that.consumoM3 &&
                Objects.equals(id, that.id) &&
                Objects.equals(idCliente, that.idCliente) &&
                Objects.equals(periodo, that.periodo) &&
                Objects.equals(valorPagar, that.valorPagar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idCliente, periodo, consumoM3, valorPagar);
    }
}
