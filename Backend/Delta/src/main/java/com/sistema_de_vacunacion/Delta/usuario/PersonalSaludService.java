package com.sistema_de_vacunacion.Delta.usuario;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalSaludService {

    private final PersonalSaludRepository personalSaludRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listarCiudadanos() {
        return ciudadanoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public UsuarioDTO registrarCiudadanoPorPersonal(UsuarioDTO dto) {
        Ciudadano ciudadano = new Ciudadano();

        ciudadano.setNombre(dto.getNombre());
        ciudadano.setApellido(dto.getApellido());
        ciudadano.setNumeroDocumento(dto.getNumeroDocumento());
        ciudadano.setTipoDocumento(dto.getTipoDocumento());
        ciudadano.setEmail(dto.getEmail());
        ciudadano.setFechaNacimiento(dto.getFechaNacimiento());
        ciudadano.setGenero(dto.getGenero());
        ciudadano.setTelefono(dto.getTelefono());
        ciudadano.setDireccion(dto.getDireccion());
        String ultimos4Digitos = extraer4UltimosDigitos(dto.getNumeroDocumento());
        String contrasenaEncriptada = passwordEncoder.encode(ultimos4Digitos);
        ciudadano.setContrasena(contrasenaEncriptada);

        ciudadano.setEstado(EstadoUsuario.ACTIVO);

        Ciudadano guardado = ciudadanoRepository.save(ciudadano);

        dto.setId(guardado.getId());
        return convertirADTO(guardado);
    }

    public UsuarioDTO obtenerCiudadanoPorDocumento(String documento) {

        Ciudadano ciudadano = ciudadanoRepository
                .findByNumeroDocumento(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ciudadano no encontrado con documento: " + documento));

        return convertirADTO(ciudadano);
    }

    // Actualizar datos de contacto/dirección del ciudadano durante la consulta
    public void actualizarDatosCiudadano(Long idCiudadano, UsuarioDTO dto) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        ciudadano.setTelefono(dto.getTelefono());
        ciudadano.setDireccion(dto.getDireccion());
        ciudadanoRepository.save(ciudadano);
    }

    private String extraer4UltimosDigitos(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.length() < 4) {
            throw new IllegalArgumentException("Documento inválido: debe tener al menos 4 dígitos");
        }
        return numeroDocumento.substring(numeroDocumento.length() - 4);
    }

    private UsuarioDTO convertirADTO(Ciudadano ciudadano) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(ciudadano.getId());
        dto.setNombre(ciudadano.getNombre());
        dto.setApellido(ciudadano.getApellido());
        dto.setNumeroDocumento(ciudadano.getNumeroDocumento());
        dto.setTipoDocumento(ciudadano.getTipoDocumento());
        dto.setEmail(ciudadano.getEmail());
        dto.setFechaNacimiento(ciudadano.getFechaNacimiento());
        dto.setGenero(ciudadano.getGenero());
        dto.setTelefono(ciudadano.getTelefono());
        dto.setDireccion(ciudadano.getDireccion());
        dto.setEstado(ciudadano.getEstado());
        return dto;
    }
}