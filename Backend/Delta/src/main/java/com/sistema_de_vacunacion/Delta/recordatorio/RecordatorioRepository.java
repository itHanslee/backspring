package com.sistema_de_vacunacion.Delta.recordatorio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RecordatorioRepository extends JpaRepository<Recordatorio, Integer> {

    List<Recordatorio> findByEstado(String estado);
}