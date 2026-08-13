package com.sistema_de_vacunacion.Delta.vacuna.strategy;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;

@Service
public class ServicioCalculadorEsquema {

    private final List<EstrategiaCalculoEsquema> estrategias;

    public ServicioCalculadorEsquema(List<EstrategiaCalculoEsquema> estrategias) {
        this.estrategias = estrategias;
    }

    public LocalDate calcularProximaFecha(
            EsquemaVacunacion esquema,
            LocalDate fechaNacimiento,
            LocalDate fechaUltimaDosis) {

        return estrategias.stream()
                .filter(estrategia ->
                        estrategia.getCriterio() == esquema.getCriterioCalculo())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una estrategia para el criterio: "
                                + esquema.getCriterioCalculo()))
                .calcularProximaFecha(
                        esquema,
                        fechaNacimiento,
                        fechaUltimaDosis);
    }
}