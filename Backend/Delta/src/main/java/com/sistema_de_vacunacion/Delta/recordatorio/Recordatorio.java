package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorio")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recordatorio")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudadano", nullable = false)
    private Ciudadano ciudadano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_esquema", nullable = false)
    private EsquemaVacunacion esquema;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(length = 255)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRecordatorio estado;

    public Recordatorio() {}

    public static RecordatorioBuilder builder() {
        return new RecordatorioBuilder();
    }

    // Getters
    public Integer getId() { return id; }
    public Ciudadano getCiudadano() { return ciudadano; }
    public EsquemaVacunacion getEsquema() { return esquema; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public String getMensaje() { return mensaje; }
    public EstadoRecordatorio getEstado() { return estado; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setCiudadano(Ciudadano ciudadano) { this.ciudadano = ciudadano; }
    public void setEsquema(EsquemaVacunacion esquema) { this.esquema = esquema; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setEstado(EstadoRecordatorio estado) { this.estado = estado; }

    public static class RecordatorioBuilder {
        private Ciudadano ciudadano;
        private EsquemaVacunacion esquema;
        private LocalDateTime fechaProgramada;
        private LocalDateTime fechaEnvio;
        private String mensaje;
        private EstadoRecordatorio estado;

        public RecordatorioBuilder ciudadano(Ciudadano ciudadano) { this.ciudadano = ciudadano; return this; }
        public RecordatorioBuilder esquema(EsquemaVacunacion esquema) { this.esquema = esquema; return this; }
        public RecordatorioBuilder fechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; return this; }
        public RecordatorioBuilder fechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; return this; }
        public RecordatorioBuilder mensaje(String mensaje) { this.mensaje = mensaje; return this; }
        public RecordatorioBuilder estado(EstadoRecordatorio estado) { this.estado = estado; return this; }

        public Recordatorio build() {
            Recordatorio r = new Recordatorio();
            r.ciudadano = this.ciudadano;
            r.esquema = this.esquema;
            r.fechaProgramada = this.fechaProgramada;
            r.fechaEnvio = this.fechaEnvio;
            r.mensaje = this.mensaje;
            r.estado = this.estado;
            return r;
        }
    }
}