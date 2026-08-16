package com.sistema_de_vacunacion.Delta.reporte;

import com.sistema_de_vacunacion.Delta.reporte.dto.ReporteRequest;

public interface ReporteService {

    byte[] generarReporte(ReporteRequest request);
}