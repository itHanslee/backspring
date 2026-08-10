package com.sistema_de_vacunacion.Delta.usuario;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    // Buscar administrador por correo para autenticación
    Optional<Administrador> findByEmail(String email);

    // Buscar administrador por documento
    Optional<Administrador> findByNumeroDocumento(String numeroDocumento);
}