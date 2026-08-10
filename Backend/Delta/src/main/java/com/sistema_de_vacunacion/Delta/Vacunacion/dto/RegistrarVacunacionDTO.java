package com.sistema_de_vacunacion.Delta.vacunacion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarVacunacionDTO {

    @NotNull(message = "El ID del ciudadano es obligatorio")
    private Long idCiudadano;

    @NotNull(message = "El ID de la vacuna es obligatorio")
    private Integer idVacuna;

    @NotBlank(message = "El lote es obligatorio")
    private String lote;

    @NotNull(message = "El número de dosis es obligatorio")
    @Min(value = 1, message = "La dosis debe ser al menos 1")
    private Integer dosis;

    private String observaciones;
}