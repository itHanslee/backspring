package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.vacunacion.dto.RegistrarVacunacionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacunaciones")
@RequiredArgsConstructor
public class VacunacionController {

    private final VacunacionService vacunacionService;

    @PostMapping
    @PreAuthorize("hasRole('PERSONAL_SALUD')")
    public ResponseEntity<Void> registrarDosis(@Valid @RequestBody RegistrarVacunacionDTO dto,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        vacunacionService.registrarAplicacion(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}