package com.sistema_de_vacunacion.Delta.usuario;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface CiudadanoRepository extends JpaRepository<Ciudadano, Integer> {

    // Buscar ciudadano por número de documento (Usado por PersonalSalud antes de aplicar dosis)
    Optional<Ciudadano> findByNumeroDocumento(String numeroDocumento);

    // Buscar por tipo y número de documento (Para validación exacta en puesto de salud)
    Optional<Ciudadano> findByTipoDocumentoAndNumeroDocumento(
            com.sistema_de_vacunacion.Delta.usuario.enums.TipoDocumento tipoDocumento, 
            String numeroDocumento
    );

    // Buscar ciudadanos registrados por coincidencia de nombre o apellido
    List<Ciudadano> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    // Verificar si ya existe un ciudadano con ese documento
    boolean existsByNumeroDocumento(String numeroDocumento);
}