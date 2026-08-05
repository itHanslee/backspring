package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.PersonalSaludDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        personal.setEstado(EstadoUsuario.ACTIVO);

        return personalSaludRepository.save(personal);
    }

    // Inactivación de cuentas de personal de salud
    public void cambiarEstadoPersonal(Long idPersonal, EstadoUsuario nuevoEstado) {
        PersonalSalud personal = personalSaludRepository.findById(idPersonal)
                .orElseThrow(() -> new RuntimeException("Personal de salud no encontrado"));
        personal.setEstado(nuevoEstado);
        personalSaludRepository.save(personal);
    }
}