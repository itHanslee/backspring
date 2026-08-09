package com.sistema_de_vacunacion.Delta.vacunacion;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface VacunacionRepository extends JpaRepository<Vacunacion, Integer> {
    List<Vacunacion> findByActivaTrue();
}