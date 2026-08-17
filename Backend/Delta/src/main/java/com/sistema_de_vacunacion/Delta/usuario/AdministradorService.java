package com.sistema_de_vacunacion.Delta.usuario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema_de_vacunacion.Delta.auditoria.AuditoriaService;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.dto.PersonalSaludDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministradorService {

    private final PersonalSaludRepository personalSaludRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
   
    @Transactional(readOnly = true)
    public List<PersonalSaludDTO> listarPersonalSalud() {
        return personalSaludRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ MODIFICADO: Ahora retorna DTO
    public PersonalSaludDTO registrarPersonalSalud(PersonalSaludDTO dto) {
        PersonalSalud personal = new PersonalSalud();
        personal.setNombre(dto.getNombre());
        personal.setApellido(dto.getApellido());
        personal.setEmail(dto.getEmail());
        personal.setNumeroDocumento(dto.getNumeroDocumento());
        personal.setTipoDocumento(dto.getTipoDocumento());
        personal.setCargo(dto.getCargo());
        personal.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        personal.setDireccion(dto.getDireccion());
        personal.setFechaNacimiento(dto.getFechaNacimiento());
        personal.setGenero(dto.getGenero());
        personal.setTelefono(dto.getTelefono());
   
        personal.setEstado(EstadoUsuario.ACTIVO);

        PersonalSalud guardado = personalSaludRepository.save(personal);
        PersonalSaludDTO respuesta = convertirADTO(guardado);          
        registrarAuditoria(
                TipoAccionAuditoria.CREAR,
                "personal_salud",
                null,
                respuesta
        );
        return respuesta;  
    }

    // ✅ NUEVO: Actualizar personal de salud
    public PersonalSaludDTO actualizarPersonalSalud(Long id, PersonalSaludDTO dto) {
        PersonalSalud personal = personalSaludRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Personal no encontrado"));
        PersonalSaludDTO datosAnteriores = convertirADTO(personal);
        personal.setNombre(dto.getNombre());
        personal.setApellido(dto.getApellido());
        personal.setEmail(dto.getEmail());
        personal.setTelefono(dto.getTelefono());
        personal.setDireccion(dto.getDireccion());
        personal.setCargo(dto.getCargo());

        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            personal.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

        PersonalSalud actualizado = personalSaludRepository.save(personal);
        PersonalSaludDTO datosNuevos = convertirADTO(actualizado);       
        registrarAuditoria(
                TipoAccionAuditoria.EDITAR,
                "personal_salud",
                datosAnteriores,
                datosNuevos
                
        );
        return datosNuevos;
    }

    // ✅ EXISTENTE: Cambiar estado (ya estaba)
    public void cambiarEstadoPersonal(Long idPersonal, EstadoUsuario nuevoEstado) {
        PersonalSalud personal = personalSaludRepository.findById(idPersonal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Personal no encontrado"));
        EstadoUsuario estadoAnterior = personal.getEstado();        
        personal.setEstado(nuevoEstado);
        PersonalSalud actualizado = personalSaludRepository.save(personal);

        registrarAuditoria(
                TipoAccionAuditoria.EDITAR,
                "personal_salud",
                "Estado anterior: " + estadoAnterior,
                "Estado nuevo: " + nuevoEstado
        );
    }

    // ✅ UTILIDAD: Método privado para convertir a DTO
    private PersonalSaludDTO convertirADTO(PersonalSalud personal) {
        PersonalSaludDTO dto = new PersonalSaludDTO();
        dto.setId(personal.getId());
        dto.setNombre(personal.getNombre());
        dto.setApellido(personal.getApellido());
        dto.setEmail(personal.getEmail());
        dto.setNumeroDocumento(personal.getNumeroDocumento());
        dto.setTipoDocumento(personal.getTipoDocumento());
        dto.setTelefono(personal.getTelefono());
        dto.setDireccion(personal.getDireccion());
        dto.setEstado(personal.getEstado());
        dto.setCargo(personal.getCargo());
        // NO incluimos la contraseña en la respuesta
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