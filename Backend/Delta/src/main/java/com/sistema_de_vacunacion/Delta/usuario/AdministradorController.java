package com.sistema_de_vacunacion.Delta.usuario;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema_de_vacunacion.Delta.usuario.dto.PersonalSaludDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdministradorController {

    private final AdministradorService administradorService;

   @GetMapping("/personal-salud")
    public ResponseEntity<List<PersonalSaludDTO>> listarPersonalSalud() {
    return ResponseEntity.ok(
            administradorService.listarPersonalSalud()
    );
}

    @PostMapping("/personal-salud")
    public ResponseEntity<PersonalSaludDTO> registrarPersonalSalud(
            @RequestBody PersonalSaludDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(administradorService.registrarPersonalSalud(dto));
    }

    @PutMapping("/personal-salud/{id}")
    public ResponseEntity<PersonalSaludDTO> actualizarPersonalSalud(
            @PathVariable Long id,
            @RequestBody PersonalSaludDTO dto) {

        return ResponseEntity.ok(
                administradorService.actualizarPersonalSalud(id, dto));
    }

    @PatchMapping("/personal-salud/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoPersonal(
            @PathVariable Long id,
            @RequestParam EstadoUsuario estado) {

        administradorService.cambiarEstadoPersonal(id, estado);
        return ResponseEntity.noContent().build();
    }
}