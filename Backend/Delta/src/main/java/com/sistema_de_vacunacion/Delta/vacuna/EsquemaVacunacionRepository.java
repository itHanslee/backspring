package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



public interface EsquemaVacunacionRepository extends JpaRepository<EsquemaVacunacion, Integer> {
    List<EsquemaVacunacion> findByVacunaIdOrderByDosisNumeroAsc(Integer idVacuna);
}