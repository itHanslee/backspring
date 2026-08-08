package com.sistema_de_vacunacion.Delta.recordatorio.dto;
import java.time.LocalDateTime;

import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;

import lombok.Data;

@Data
public class RecordatorioDTO {

    private Integer id;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEnvio;
    private String mensaje;
    private EstadoRecordatorio estado;
    private int idUsuario; 
    private Integer idEsquema;
}
