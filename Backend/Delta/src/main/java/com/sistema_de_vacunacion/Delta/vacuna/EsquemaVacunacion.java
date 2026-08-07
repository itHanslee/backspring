package com.sistema_de_vacunacion.Delta.vacuna;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;
import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;
import com.sistema_de_vacunacion.Delta.vacuna.enums.UnidadTiempo;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "dosis_numero", nullable = false)
    private NumeroDosis dosisNumero;

    //Reglas de calculo por edad 
    @Column(name = "edad_minima_aplicacion", nullable = false)
    private Integer edadMinimaAplicacion;

    @Column(name = "edad_maxima_aplicacion", nullable = false)
    private Integer edadMaximaAplicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_tiempo_edad")
    private UnidadTiempo unidadTiempoEdad;

    //Reglas por intervalo
    @Column(name = "intervalo_dias")
    private Integer intervaloDias;

    @Enumerated(EnumType.STRING)
    @Column(name = "criterio_calculo", nullable = false)
    private CriterioCalculo criterioCalculo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vacuna vacuna;


}