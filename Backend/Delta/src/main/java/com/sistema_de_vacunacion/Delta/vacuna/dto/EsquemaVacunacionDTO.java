package com.sistema_de_vacunacion.Delta.vacuna.dto;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;
import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;

import lombok.Data;

@Data
public class EsquemaVacunacionDTO {
    private Integer id;
    private Integer edadMinimaAplicacion;
    private NumeroDosis numeroDosis;
    private Integer intervaloDias;
    private CriterioCalculo criterioCalculo;
    private Integer idVacuna;
}
