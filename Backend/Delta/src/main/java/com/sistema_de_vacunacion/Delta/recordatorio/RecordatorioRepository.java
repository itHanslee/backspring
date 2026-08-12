package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Integer> {

    List<Recordatorio> findByEstado(EstadoRecordatorio estado);
}