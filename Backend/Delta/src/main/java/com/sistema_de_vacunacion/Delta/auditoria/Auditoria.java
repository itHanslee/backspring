package com.sistema_de_vacunacion.Delta.auditoria;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer id;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String tipoAccion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    private String detalle;
}