package com.sistema_de_vacunacion.Delta.reporte.dto;

import com.sistema_de_vacunacion.Delta.reporte.enums.FormatoReporte;
import com.sistema_de_vacunacion.Delta.reporte.enums.TipoReporte;

import java.time.LocalDate;

public class ReporteRequest {

    private LocalDate fechaDesde;

    private LocalDate fechaHasta;

    private TipoReporte tipoReporte;

    private FormatoReporte formato;

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public TipoReporte getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public FormatoReporte getFormato() {
        return formato;
    }

    public void setFormato(FormatoReporte formato) {
        this.formato = formato;
    }
}