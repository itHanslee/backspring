package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ciudadanos")
@RequiredArgsConstructor
public class CiudadanoController {

    private final CiudadanoService ciudadanoService;

    @GetMapping("/{id}/perfil")
    public ResponseEntity<UsuarioDTO> perfil(@PathVariable Integer id) {
        return ResponseEntity.ok(ciudadanoService.obtenerPerfilCiudadano(id));
    }

    @GetMapping(value = "/{id}/carne", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarCarne(@PathVariable Integer id) {
        return ResponseEntity.ok(ciudadanoService.generarCarneVacunacionPDF(id));
    }
}