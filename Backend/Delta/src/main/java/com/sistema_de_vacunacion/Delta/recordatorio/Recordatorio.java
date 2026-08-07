package com.sistema_de_vacunacion.Delta.recordatorio;

import java.time.LocalDateTime;

import com.sistema_de_vacunacion.Delta.usuario.Usuario;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recordatorio")
@Getter
@Setter
@NoArgsConstructor
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recordatorio")
    private Integer id;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(length = 255)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRecordatorio estado = EstadoRecordatorio.Pendiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_esquema", nullable = false)
    private EsquemaVacunacion esquema;

    public void marcarComoEnviado() {
        this.estado = EstadoRecordatorio.Enviado;
        this.fechaEnvio = LocalDateTime.now();
    }

    public void marcarComoFallido() {
        this.estado = EstadoRecordatorio.Fallido;
    }
}