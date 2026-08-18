package com.sistema_de_vacunacion.Delta.vacunacion.dto;

import java.time.LocalDate;

import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;

public class RegistrarVacunacionDTO {

    private Long idCiudadano;
    private Integer idInventario;
    private NumeroDosis dosis;
    private LocalDate fechaAplicacion;
    private String observaciones;
    private boolean reaccionesAdversas;

    public RegistrarVacunacionDTO() {}

    public Long getIdCiudadano() {
        return idCiudadano;
    }

    public void setIdCiudadano(Long idCiudadano) {
        this.idCiudadano = idCiudadano;
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public NumeroDosis getDosis() {
        return dosis;
    }

    public void setDosis(NumeroDosis dosis) {
        this.dosis = dosis;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isReaccionesAdversas() {
        return reaccionesAdversas;
    }

    public void setReaccionesAdversas(boolean reaccionesAdversas) {
        this.reaccionesAdversas = reaccionesAdversas;
    }
}