package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;

    public UsuarioDTO obtenerPerfilCiudadano(Long idCiudadano) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));
        return mapearADTO(ciudadano);
    }

    // Método para generar la estructura del carné digital en formato DTO/PDF
    public byte[] generarCarneVacunacionPDF(Long idCiudadano) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));
       
        return new byte[0]; 
    }

    private UsuarioDTO mapearADTO(Ciudadano c) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setEmail(c.getEmail());
        dto.setNumeroDocumento(c.getNumeroDocumento());
        dto.setTipoUsuario(c.getPermisos());
        return dto;
    }
}