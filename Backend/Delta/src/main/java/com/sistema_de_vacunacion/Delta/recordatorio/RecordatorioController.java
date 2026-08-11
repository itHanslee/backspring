package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;
import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recordatorios")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;

    public RecordatorioController(RecordatorioService recordatorioService) {
        this.recordatorioService = recordatorioService;
    }

    @PostMapping
    public ResponseEntity<RecordatorioDTO> crear(@RequestBody RecordatorioDTO dto) {
        return new ResponseEntity<>(recordatorioService.crearRecordatorio(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RecordatorioDTO>> listarTodos() {
        return ResponseEntity.ok(recordatorioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordatorioDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(recordatorioService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RecordatorioDTO>> buscarPorEstado(@PathVariable EstadoRecordatorio estado) {
        return ResponseEntity.ok(recordatorioService.buscarPorEstado(estado));
    }

    @PatchMapping("/{id}/enviado")
    public ResponseEntity<Void> marcarComoEnviado(@PathVariable Integer id) {
        recordatorioService.marcarComoEnviado(id);
        return ResponseEntity.noContent().build();
    }
}