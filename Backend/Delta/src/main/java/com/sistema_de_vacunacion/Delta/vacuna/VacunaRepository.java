package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Integer> {
    Optional<Vacuna> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}