package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.vacunacion.dto.RegistrarVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunacionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunaPendienteDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/vacunaciones")
public class VacunacionController {

    private final VacunacionService vacunacionService;

    public VacunacionController(VacunacionService vacunacionService) {
        this.vacunacionService = vacunacionService;
    }

    // RF-05: Registrar aplicación de vacuna
    @PostMapping
    public ResponseEntity<Void> registrar(
            @RequestBody RegistrarVacunacionDTO dto,
            Authentication authentication) {

        String emailPersonal = authentication.getName();
        vacunacionService.registrarAplicacion(dto, emailPersonal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // RF-07: Ver vacunas aplicadas a un ciudadano
    @GetMapping("/ciudadano/{idCiudadano}")
    public ResponseEntity<List<VacunacionResponseDTO>> obtenerVacunasAplicadas(
            @PathVariable Long idCiudadano) {
        return ResponseEntity.ok(vacunacionService.obtenerVacunasAplicadas(idCiudadano));
    }

    // RF-04: Historial cronológico
    @GetMapping("/ciudadano/{idCiudadano}/historial")
    public ResponseEntity<List<VacunacionResponseDTO>> obtenerHistorial(
            @PathVariable Long idCiudadano) {
        return ResponseEntity.ok(vacunacionService.obtenerHistorial(idCiudadano));
    }

    @GetMapping("/ciudadano/{idCiudadano}/pendientes")
    @PreAuthorize("hasRole('PERSONAL_SALUD')")
    public ResponseEntity<List<VacunaPendienteDTO>> obtenerVacunasPendientes(
            @PathVariable Long idCiudadano) {

        return ResponseEntity.ok(
                vacunacionService.obtenerVacunasPendientes(idCiudadano));
    }
}