package com.sistema_de_vacunacion.Delta.reporte;

import com.sistema_de_vacunacion.Delta.reporte.dto.ReporteRequest;
import com.sistema_de_vacunacion.Delta.reporte.enums.FormatoReporte;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PERSONAL_SALUD')")
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/vacunaciones")
    public ResponseEntity<byte[]> generarReporte(
            @RequestBody ReporteRequest request) {

        byte[] archivo = reporteService.generarReporte(request);

        if (request.getFormato() == FormatoReporte.PDF) {

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=reporte-vacunaciones.pdf"
                    )
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(archivo);
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte-vacunaciones.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(archivo);
    }
}