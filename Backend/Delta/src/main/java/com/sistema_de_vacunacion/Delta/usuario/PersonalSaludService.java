package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.PersonalSaludDTO;
import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalSaludService {

    private final PersonalSaludRepository personalSaludRepository;
    private final CiudadanoRepository ciudadanoRepository;

    // Registrar un nuevo ciudadano directamente desde el puesto de vacunación
    public UsuarioDTO registrarCiudadanoPorPersonal(UsuarioDTO dto) {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setNombre(dto.getNombre());
        ciudadano.setApellido(dto.getApellido());
        ciudadano.setNumeroDocumento(dto.getNumeroDocumento());
        ciudadano.setTipoDocumento(dto.getTipoDocumento());
        ciudadano.setEmail(dto.getEmail());
        ciudadano.setFechaNacimiento(dto.getFechaNacimiento());
        ciudadano.setGenero(dto.getGenero());

        Ciudadano guardado = ciudadanoRepository.save(ciudadano);
        
        dto.setId(guardado.getId());
        return dto;
    }

    // Actualizar datos de contacto/dirección del ciudadano durante la consulta
    public void actualizarDatosCiudadano(Integer idCiudadano, UsuarioDTO dto) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));
        
        ciudadano.setTelefono(dto.getTelefono());
        ciudadano.setDireccion(dto.getDireccion());
        ciudadanoRepository.save(ciudadano);
    }
}