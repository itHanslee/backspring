package com.sistema_de_vacunacion.Delta.usuario;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ciudadanos")
@RequiredArgsConstructor
public class CiudadanoController {

    private final CiudadanoService ciudadanoService;

    @GetMapping("/{id}/perfil")
    public ResponseEntity<UsuarioDTO> perfil(@PathVariable Long id) {
        return ResponseEntity.ok(ciudadanoService.obtenerPerfilCiudadano(id));
    }

    @GetMapping(value = "/{id}/carne", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarCarne(@PathVariable Long id) {
    byte[] pdf = ciudadanoService.generarCarneVacunacionPDF(id);

    return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}