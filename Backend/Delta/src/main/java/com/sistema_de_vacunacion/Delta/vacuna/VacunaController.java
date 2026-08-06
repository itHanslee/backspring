package com.sistema_de_vacunacion.Delta.vacuna;

import com.sistema_de_vacunacion.Delta.vacuna.dto.EsquemaVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.InventarioLoteDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.VacunaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacunas")
public class VacunaController {

    private final VacunaService vacunaService;

    public VacunaController(VacunaService vacunaService) {
        this.vacunaService = vacunaService;
    }

    // --- ENDPOINTS VACUNA ---

    @PostMapping
    public ResponseEntity<VacunaDTO> crear(@RequestBody VacunaDTO dto) {
        return new ResponseEntity<>(vacunaService.crearVacuna(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VacunaDTO>> listarTodas() {
        return ResponseEntity.ok(vacunaService.listarTodasLasVacunas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacunaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(vacunaService.obtenerVacunaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacunaDTO> actualizar(@PathVariable Integer id, @RequestBody VacunaDTO dto) {
        return ResponseEntity.ok(vacunaService.actualizarVacuna(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Integer id, @RequestParam String estado) {
        vacunaService.cambiarEstadoVacuna(id, estado);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINTS INVENTARIO LOTE ---

    @PostMapping("/lotes")
    public ResponseEntity<InventarioLoteDTO> registrarLote(@RequestBody InventarioLoteDTO dto) {
        return new ResponseEntity<>(vacunaService.registrarLote(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/lotes")
    public ResponseEntity<List<InventarioLoteDTO>> listarLotesPorVacuna(@PathVariable Integer id) {
        return ResponseEntity.ok(vacunaService.listarLotesPorVacuna(id));
    }

    // --- ENDPOINTS ESQUEMA DE VACUNACIÓN ---

    @PostMapping("/{id}/esquemas")
    public ResponseEntity<EsquemaVacunacionDTO> registrarEsquema(
            @PathVariable Integer id, 
            @RequestBody EsquemaVacunacionDTO dto) {
        dto.setIdVacuna(id);
        return new ResponseEntity<>(vacunaService.registrarEsquema(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/esquemas")
    public ResponseEntity<List<EsquemaVacunacionDTO>> obtenerEsquemasPorVacuna(@PathVariable Integer id) {
        return ResponseEntity.ok(vacunaService.obtenerEsquemasPorVacuna(id));
    }

    @DeleteMapping("/esquemas/{idEsquema}")
    public ResponseEntity<Void> eliminarEsquema(@PathVariable Integer idEsquema) {
        vacunaService.eliminarEsquema(idEsquema);
        return ResponseEntity.noContent().build();
    }
}