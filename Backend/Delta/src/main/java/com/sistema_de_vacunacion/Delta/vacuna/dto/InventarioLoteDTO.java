package com.sistema_de_vacunacion.Delta.vacuna.dto;
import java.time.LocalDate;

import lombok.Data;

@Data
public class InventarioLoteDTO {
    private Integer id;
    private String numeroLote;
    private Integer cantidadRecibida;
    private Integer stockActual;
    private LocalDate fechaVencimiento;
    private boolean activo;
    private Integer idVacuna;

}
