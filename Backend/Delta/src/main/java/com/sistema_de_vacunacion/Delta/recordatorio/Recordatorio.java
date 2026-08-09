package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.vacunacion.Vacunacion;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "recordatorio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recordatorio")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudadano", nullable = false)
    private Ciudadano ciudadano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vacunacion vacuna;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(length = 20)
    private String estado; // Pendiente, Enviado, Cancelado
}