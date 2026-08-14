package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.PersonalSaludDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministradorService {

    private final PersonalSaludRepository personalSaludRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ NUEVO: Listar todo el personal de salud
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
        personal.setEstado(EstadoUsuario.ACTIVO);

        PersonalSalud guardado = personalSaludRepository.save(personal);
        return convertirADTO(guardado);  // ← Retorna DTO
    }

    // ✅ NUEVO: Actualizar personal de salud
    public PersonalSaludDTO actualizarPersonalSalud(Long id, PersonalSaludDTO dto) {
        PersonalSalud personal = personalSaludRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Personal no encontrado"));

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
        return convertirADTO(actualizado);
    }

    // ✅ EXISTENTE: Cambiar estado (ya estaba)
    public void cambiarEstadoPersonal(Long idPersonal, EstadoUsuario nuevoEstado) {
        PersonalSalud personal = personalSaludRepository.findById(idPersonal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Personal no encontrado"));
        personal.setEstado(nuevoEstado);
        personalSaludRepository.save(personal);
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
}