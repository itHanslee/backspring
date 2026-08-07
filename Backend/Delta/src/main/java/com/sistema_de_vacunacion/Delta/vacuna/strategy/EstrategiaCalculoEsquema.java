package com.sistema_de_vacunacion.Delta.vacuna.strategy;

import java.time.LocalDate;

import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;

public interface EstrategiaCalculoEsquema {
    CriterioCalculo getCriterio();
    LocalDate calcularProximaFecha(EsquemaVacunacion esquema, LocalDate fechaNacimiento, LocalDate fechaUltimaDosis);
}