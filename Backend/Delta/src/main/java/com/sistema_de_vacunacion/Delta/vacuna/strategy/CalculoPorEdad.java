package com.sistema_de_vacunacion.Delta.vacuna.strategy;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.enums.CriterioCalculo;

@Component
public class CalculoPorEdad implements EstrategiaCalculoEsquema {

    @Override
    public CriterioCalculo getCriterio() {
        return CriterioCalculo.POR_EDAD;
    }

    @Override
    public LocalDate calcularProximaFecha(EsquemaVacunacion esquema, LocalDate fechaNacimiento, LocalDate fechaUltimaDosis) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es requerida para cálculo POR_EDAD");
        }
        
        if (esquema.getEdadMinimaAplicacion() == null || esquema.getUnidadTiempoEdad() == null) {
            return fechaNacimiento;
        }

        return switch (esquema.getUnidadTiempoEdad()) {
            case DIAS -> fechaNacimiento.plusDays(esquema.getEdadMinimaAplicacion());
            case SEMANAS -> fechaNacimiento.plusWeeks(esquema.getEdadMinimaAplicacion());
            case MESES -> fechaNacimiento.plusMonths(esquema.getEdadMinimaAplicacion());
            case AÑOS -> fechaNacimiento.plusYears(esquema.getEdadMinimaAplicacion());
        };
    }
}