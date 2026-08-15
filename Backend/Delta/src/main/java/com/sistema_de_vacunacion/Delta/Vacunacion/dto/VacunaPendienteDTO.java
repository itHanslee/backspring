package com.sistema_de_vacunacion.Delta.vacunacion.dto;

import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VacunaPendienteDTO {

    private Long idCiudadano;
    private Integer idInventario;

    private String nombreCiudadano;
    private String documentoCiudadano;

    private String vacunaNombre;
    private String numeroLote;

    private NumeroDosis dosis;
    private String descripcion;

    private LocalDate fechaProgramada;
}