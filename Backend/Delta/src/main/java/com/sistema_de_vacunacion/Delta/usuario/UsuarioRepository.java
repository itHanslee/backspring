package com.sistema_de_vacunacion.Delta.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
}