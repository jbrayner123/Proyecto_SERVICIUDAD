package com.serviciudad.serviciudad.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class FacturaEnergia {
    private String idCliente;
    private String periodo;
    private int consumoKwh;
    private BigDecimal valorPagar;

    public FacturaEnergia(String idCliente, String periodo, int consumoKwh, BigDecimal valorPagar) {
        this.idCliente = idCliente;
        this.periodo = periodo;
        this.consumoKwh = consumoKwh;
        this.valorPagar = valorPagar;
    }

    public String getIdCliente() { return idCliente; }
    public String getPeriodo() { return periodo; }
    public int getConsumoKwh() { return consumoKwh; }
    public BigDecimal getValorPagar() { return valorPagar; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacturaEnergia)) return false;
        FacturaEnergia that = (FacturaEnergia) o;
        return consumoKwh == that.consumoKwh &&
                Objects.equals(idCliente, that.idCliente) &&
                Objects.equals(periodo, that.periodo) &&
                Objects.equals(valorPagar, that.valorPagar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente, periodo, consumoKwh, valorPagar);
    }
}
