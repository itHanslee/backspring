package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.PersonalSaludDTO;
import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdministradorController {

    private final AdministradorService administradorService;

    @PostMapping("/personal-salud")
    public ResponseEntity<PersonalSalud> registrarPersonalSalud(@RequestBody PersonalSaludDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorService.registrarPersonalSalud(dto));
    }

    @PatchMapping("/personal-salud/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoPersonal(@PathVariable Long id, @RequestParam EstadoUsuario estado) {
        administradorService.cambiarEstadoPersonal(id, estado);
        return ResponseEntity.noContent().build();
    }
}