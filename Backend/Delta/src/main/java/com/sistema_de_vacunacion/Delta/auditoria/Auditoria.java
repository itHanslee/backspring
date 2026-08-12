package com.sistema_de_vacunacion.Delta.auditoria;

import java.time.LocalDateTime;

import com.sistema_de_vacunacion.Delta.usuario.Usuario;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false, length = 20)
    private TipoAccionAuditoria tipoAccion;

    @Column(name = "tabla_afectada", nullable = false, length = 100)
    private String tablaAfectada;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // NUEVOS CAMPOS
    @Column(name = "datos_anteriores", columnDefinition = "TEXT")
    private String datosAnteriores;

    @Column(name = "datos_nuevos", columnDefinition = "TEXT")
    private String datosNuevos;
}