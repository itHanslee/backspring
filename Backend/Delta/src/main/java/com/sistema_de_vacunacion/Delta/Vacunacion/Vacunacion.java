package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSalud;
import com.sistema_de_vacunacion.Delta.vacuna.Vacuna;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vacunacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacunacion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudadano", nullable = false)
    private Ciudadano ciudadano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal", nullable = false)
    private PersonalSalud personalSalud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vacuna vacuna;

    @Column(nullable = false, length = 50)
    private String lote;

    @Column(nullable = false)
    private Integer dosis;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(length = 255)
    private String observaciones;
}