package com.sistema_de_vacunacion.Delta.recordatorio.dto;

import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;

import java.time.LocalDateTime;

public class RecordatorioDTO {

    private Integer id;
    private Long idCiudadano;
    private Integer idEsquema;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEnvio;
    private String mensaje;
    private EstadoRecordatorio estado;

    public RecordatorioDTO() {}

    // Getters
    public Integer getId() { return id; }
    public Long getIdCiudadano() { return idCiudadano; }
    public Integer getIdEsquema() { return idEsquema; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public String getMensaje() { return mensaje; }
    public EstadoRecordatorio getEstado() { return estado; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setIdCiudadano(Long idCiudadano) { this.idCiudadano = idCiudadano; }
    public void setIdEsquema(Integer idEsquema) { this.idEsquema = idEsquema; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setEstado(EstadoRecordatorio estado) { this.estado = estado; }
}