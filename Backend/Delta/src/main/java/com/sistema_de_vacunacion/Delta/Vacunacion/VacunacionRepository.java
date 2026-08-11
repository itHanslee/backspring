package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Integer> {

    List<Vacunacion> findByCiudadano(Ciudadano ciudadano);

    List<Vacunacion> findByCiudadanoOrderByFechaAplicacionAsc(Ciudadano ciudadano);
}