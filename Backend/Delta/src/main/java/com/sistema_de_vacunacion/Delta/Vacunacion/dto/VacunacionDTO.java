package com.vacunacion.vacunacion.dto;

import com.vacunacion.common.enums.NumeroDosis;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VacunacionDTO {
    private Long id;
    private LocalDate fechaAplicacion;
    private NumeroDosis dosis;
    private String lote;
    private String observaciones;
    private boolean reaccionAdversa;
    private Long personalSaludId;
    private Long vacunaId;
    private Long ciudadanoId;
}
