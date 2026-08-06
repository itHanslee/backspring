package com.sistema_de_vacunacion.Delta.vacuna.dto;

import com.sistema_de_vacunacion.Delta.vacuna.enums.EstadoVacuna;
import com.sistema_de_vacunacion.Delta.vacuna.enums.ViaAdministracion;

import lombok.Data;

@Data

public class VacunaDTO {

    private Integer id;
    private String codigo;
    private String nombre;
    private String fabricante;
    private Integer dosisTotales;
    private ViaAdministracion viaAdministracion;
    private Double temperaturaAlmacenamiento;
    private EstadoVacuna estado;
    
}
