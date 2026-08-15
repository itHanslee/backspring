package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personal-salud")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PERSONAL_SALUD')")
public class PersonalSaludController {

    private final PersonalSaludService personalSaludService;

    @GetMapping("/ciudadanos")
    public ResponseEntity<List<UsuarioDTO>> listarCiudadanos() {
        return ResponseEntity.ok(
                personalSaludService.listarCiudadanos());
    }

    @PostMapping("/ciudadanos")
    public ResponseEntity<UsuarioDTO> registrarCiudadano(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalSaludService.registrarCiudadanoPorPersonal(dto));
    }

    @GetMapping("/ciudadanos/documento/{documento}")
    public ResponseEntity<UsuarioDTO> buscarCiudadanoPorDocumento(
            @PathVariable String documento) {

        return ResponseEntity.ok(
                personalSaludService.obtenerCiudadanoPorDocumento(documento));
    }

    @PutMapping("/ciudadanos/{id}")
    public ResponseEntity<Void> actualizarCiudadano(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        personalSaludService.actualizarDatosCiudadano(id, dto);
        return ResponseEntity.noContent().build();
    }

}