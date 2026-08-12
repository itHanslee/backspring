package com.sistema_de_vacunacion.Delta.vacunacion.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VacunacionDTO {

    private Integer idVacunacion;
    private LocalDate fechaAplicacion;
    private Integer dosis;
    private String observaciones;
    private Boolean reaccionAdversa;

    private Long idPersonalSalud;
    private Long idCiudadano;
    private Integer idVacuna;
}