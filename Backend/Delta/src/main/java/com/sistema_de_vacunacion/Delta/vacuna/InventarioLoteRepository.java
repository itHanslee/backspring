package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface InventarioLoteRepository extends JpaRepository<InventarioLote, Integer> {
    List<InventarioLote> findByVacunaIdAndActivoTrue(Integer idVacuna);
    List<InventarioLote> findByNumeroLote(String numeroLote);
}
