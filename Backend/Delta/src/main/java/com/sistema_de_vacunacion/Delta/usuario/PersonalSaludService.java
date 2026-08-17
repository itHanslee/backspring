package com.sistema_de_vacunacion.Delta.usuario;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema_de_vacunacion.Delta.auditoria.AuditoriaService;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalSaludService {

    private final PersonalSaludRepository personalSaludRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

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
        UsuarioDTO respuesta = convertirADTO(guardado);

        // Registrar auditoría
        registrarAuditoria(
                TipoAccionAuditoria.CREAR,
                "ciudadano",
                null,
                respuesta
        );

        return respuesta;
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
        
        UsuarioDTO datosAnteriores =
                convertirADTO(ciudadano);
                ciudadano.setEmail(dto.getEmail());
                ciudadano.setTelefono(dto.getTelefono());
                ciudadano.setDireccion(dto.getDireccion());

        Ciudadano actualizado = ciudadanoRepository.save(ciudadano);
        UsuarioDTO datosNuevos = convertirADTO(actualizado);        
        
        
           registrarAuditoria(
                TipoAccionAuditoria.EDITAR,
                "ciudadano",
                datosAnteriores,
                datosNuevos
        );
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

    private Usuario usuarioAutenticado() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNoEncontradoException(
                                "Usuario autenticado no encontrado"
                        )
                );
    }

    /**
     * Registra una acción en la tabla de auditoría.
     */
    private void registrarAuditoria(
            TipoAccionAuditoria tipo,
            String tabla,
            Object anterior,
            Object nuevo) {
        try {

            String datosAnteriores =
                    anterior == null
                            ? null
                            : objectMapper.writeValueAsString(
                                    anterior
                            );

            String datosNuevos =
                    nuevo == null
                            ? null
                            : objectMapper.writeValueAsString(
                                    nuevo
                            );

            auditoriaService.registrar(
                    tipo,
                    tabla,
                    usuarioAutenticado(),
                    datosAnteriores,
                    datosNuevos
            );
        } catch (JacksonException e) {

            throw new RuntimeException(
                    "Error al registrar auditoría",
                    e
            );
        }
    }
}