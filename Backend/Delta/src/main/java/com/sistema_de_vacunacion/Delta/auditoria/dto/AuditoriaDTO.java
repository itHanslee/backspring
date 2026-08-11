package com.sistema_de_vacunacion.Delta.auditoria.dto;

import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;

import java.time.LocalDateTime;

public class AuditoriaDTO {

    private Integer idAuditoria;
    private TipoAccionAuditoria tipoAccion;
    private String tablaAfectada;
    private LocalDateTime fechaAccion;
    private Long idUsuario;

    public AuditoriaDTO() {}

    public Integer getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Integer idAuditoria) { this.idAuditoria = idAuditoria; }

    public TipoAccionAuditoria getTipoAccion() { return tipoAccion; }
    public void setTipoAccion(TipoAccionAuditoria tipoAccion) { this.tipoAccion = tipoAccion; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public LocalDateTime getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(LocalDateTime fechaAccion) { this.fechaAccion = fechaAccion; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}