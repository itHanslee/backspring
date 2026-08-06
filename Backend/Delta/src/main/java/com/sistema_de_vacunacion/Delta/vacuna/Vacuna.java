package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;

import com.sistema_de_vacunacion.Delta.vacuna.enums.EstadoVacuna;
import com.sistema_de_vacunacion.Delta.vacuna.enums.ViaAdministracion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "vacuna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Vacuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacuna")
    private Integer id;

    @Column(nullable = false, unique= true, length = 20)
    private String codigo;

    @Column (nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String fabricante;

    @Column(name="dosis_totales", nullable = false)
    private Integer dosisTotales;

    @Enumerated(EnumType.STRING)
    @Column(name="via_administracion", nullable = false)
    private ViaAdministracion viaAdministracion;

    @Column(name = "t_almacenamiento")
    private Double temperaturaAlmacenamiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVacuna estado;

    @OneToMany(mappedBy = "vacuna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EsquemaVacunacion> esquemas;

}
