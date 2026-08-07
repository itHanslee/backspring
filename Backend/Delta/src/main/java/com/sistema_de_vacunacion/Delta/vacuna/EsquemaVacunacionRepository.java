package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsquemaVacunacionRepository extends JpaRepository<EsquemaVacunacion, Integer> {
    List<EsquemaVacunacion> findByVacunaIdOrderByDosisNumeroAsc(Integer idVacuna);
}