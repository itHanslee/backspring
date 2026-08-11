package com.sistema_de_vacunacion.Delta.vacunacion.dto;

public class RegistrarVacunacionDTO {

    private Long idCiudadano;
    private Integer idInventario;
    private Integer dosis;
    private String observaciones;
    private boolean reaccionesAdversas;

    public RegistrarVacunacionDTO() {}

    public Long getIdCiudadano() { return idCiudadano; }
    public void setIdCiudadano(Long idCiudadano) { this.idCiudadano = idCiudadano; }

    public Integer getIdInventario() { return idInventario; }
    public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }

    public Integer getDosis() { return dosis; }
    public void setDosis(Integer dosis) { this.dosis = dosis; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isReaccionesAdversas() { return reaccionesAdversas; }
    public void setReaccionesAdversas(boolean reaccionesAdversas) { this.reaccionesAdversas = reaccionesAdversas; }
}