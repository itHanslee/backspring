package com.sistema_de_vacunacion.Delta.vacuna.strategy;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;

@Component
public class CalculoPorIntervalo implements EstrategiaCalculoEsquema {

    @Override
    public CriterioCalculo getCriterio() {
        return CriterioCalculo.POR_INTERVALO;
    }

    @Override
    public LocalDate calcularProximaFecha(EsquemaVacunacion esquema, LocalDate fechaNacimiento, LocalDate fechaUltimaDosis) {
        if (fechaUltimaDosis == null) {
            throw new IllegalArgumentException("Se requiere la fecha de la última dosis para calcular el intervalo.");
        }

        int dias = (esquema.getIntervaloDias() != null) ? esquema.getIntervaloDias() : 0;
        return fechaUltimaDosis.plusDays(dias);
    }
}