package com.sistema_de_vacunacion.Delta.usuario;


import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministradorService {

    private final PersonalSaludRepository personalSaludRepository;
    private final PasswordEncoder passwordEncoder;

    // Alta de personal médico/salud en el sistema
    public PersonalSalud registrarPersonalSalud(PersonalSaludDTO dto) {
        PersonalSalud personal = new PersonalSalud();
        personal.setNombre(dto.getNombre());
        personal.setApellido(dto.getApellido());
        personal.setEmail(dto.getEmail());
        personal.setNumeroDocumento(dto.getNumeroDocumento());
        personal.setTipoDocumento(dto.getTipoDocumento());
        personal.setCargo(dto.getCargo());
        personal.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        personal.setEstado(EstadoUsuario.Activo);

        return personalSaludRepository.save(personal);
    }

    // Inactivación de cuentas de personal de salud
    public void cambiarEstadoPersonal(Integer idPersonal, EstadoUsuario nuevoEstado) {
        PersonalSalud personal = personalSaludRepository.findById(idPersonal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Personal de salud no encontrado"));
        personal.setEstado(nuevoEstado);
        personalSaludRepository.save(personal);
    }
}