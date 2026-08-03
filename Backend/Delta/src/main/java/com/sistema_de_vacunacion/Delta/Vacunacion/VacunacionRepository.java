package com.vacunacion.vacunacion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacunacionRepository extends JpaRepository<Vacunacion, Long> {
    List<Vacunacion> findByCiudadanoId(Long ciudadanoId);
    List<Vacunacion> findByVacunaId(Long vacunaId);
}
