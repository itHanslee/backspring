package com.sistema_de_vacunacion.Delta.vacuna.dto;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;
import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;
import com.sistema_de_vacunacion.Delta.vacuna.enums.UnidadTiempo;

import lombok.Data;

@Data
public class EsquemaVacunacionDTO {
    private Integer id;
    private NumeroDosis dosisNumero;
    private Integer edadMinimaAplicacion;
    private Integer edadMaximaAplicacion;
    private UnidadTiempo unidadTiempoEdad;
    private Integer intervaloDias;
    private CriterioCalculo criterioCalculo;
    private String observaciones;
    private Integer idVacuna;
}
