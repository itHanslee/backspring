package com.vacunacion.vacunacion;

import com.vacunacion.vacunacion.dto.VacunacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacunaciones")
@RequiredArgsConstructor
public class VacunacionController {

    private final VacunacionService vacunacionService;

    @PostMapping
    public VacunacionDTO registrar(@RequestBody VacunacionDTO dto) {
        return vacunacionService.registrar(dto);
    }

    @GetMapping("/{id}")
    public VacunacionDTO buscarPorId(@PathVariable Long id) {
        return vacunacionService.buscarPorId(id);
    }

    @GetMapping("/ciudadano/{ciudadanoId}")
    public List<VacunacionDTO> historialPorCiudadano(@PathVariable Long ciudadanoId) {
        return vacunacionService.historialPorCiudadano(ciudadanoId);
    }
}
