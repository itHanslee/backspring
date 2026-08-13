package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Integer> {

    List<Recordatorio> findByEstado(EstadoRecordatorio estado);

    boolean existsByCiudadanoIdAndEsquemaId(
            Long idCiudadano,
            Integer idEsquema);
}