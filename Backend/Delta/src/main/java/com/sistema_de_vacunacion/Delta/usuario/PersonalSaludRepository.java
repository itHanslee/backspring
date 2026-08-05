package com.sistema_de_vacunacion.Delta.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface PersonalSaludRepository extends JpaRepository<PersonalSalud, Long> {

    // Buscar personal de salud por su cargo (ej: "Enfermero PAI", "Vacunador")
    List<PersonalSalud> findByCargo(String cargo);

    // Buscar personal de salud activo por su número de documento
    Optional<PersonalSalud> findByNumeroDocumentoAndEstado(
            String numeroDocumento, 
            com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario estado
    );

    // Consulta customizada: Buscar personal activo según su correo
    @Query("SELECT p FROM PersonalSalud p WHERE p.email = :email AND p.estado = 'Activo'")
    Optional<PersonalSalud> findPersonalActivoPorEmail(@Param("email") String email);
}