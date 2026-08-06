package com.sistema_de_vacunacion.Delta.vacuna;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;
import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "esquema_vacunacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EsquemaVacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_esquema")
    private Integer id;

    @Column(name = "edad_minima_aplicacion", nullable = false)
    private Integer edadMinimaAplicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "dosis_numero", nullable = false)
    private NumeroDosis numeroDosis;

   @Column(name = "intervalo_dias", nullable = false)
    private Integer intervaloDias;

    @Enumerated(EnumType.STRING)
    @Column (name = "criterio_calculo", nullable = false)
    private CriterioCalculo criterioCalculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vacuna vacuna;
}