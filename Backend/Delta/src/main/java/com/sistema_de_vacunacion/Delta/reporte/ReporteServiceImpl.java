package com.sistema_de_vacunacion.Delta.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sistema_de_vacunacion.Delta.reporte.dto.ReporteRequest;
import com.sistema_de_vacunacion.Delta.vacunacion.Vacunacion;
import com.sistema_de_vacunacion.Delta.vacunacion.VacunacionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

        private final VacunacionRepository vacunacionRepository;

        @Override
        public byte[] generarReporte(ReporteRequest request) {

                validarFechas(request);

                return switch (request.getTipoReporte()) {

                        case VACUNAS_APLICADAS ->
                                generarReporteVacunasAplicadas(request);

                        case INVENTARIO_LOTES ->
                                generarReporteInventarioLotes(request);

                        case COBERTURA_CIUDADANOS ->
                                generarReporteCoberturaCiudadanos(request);
                };
        }

        private byte[] generarReporteVacunasAplicadas(
                        ReporteRequest request) {

                LocalDateTime fechaDesde = request.getFechaDesde().atStartOfDay();

                LocalDateTime fechaHasta = request.getFechaHasta()
                                .plusDays(1)
                                .atStartOfDay()
                                .minusNanos(1);

                List<Vacunacion> vacunaciones = vacunacionRepository.findByFechaAplicacionBetween(
                                fechaDesde,
                                fechaHasta);

                return switch (request.getFormato()) {

                        case PDF ->
                                generarPdfVacunasAplicadas(
                                                vacunaciones,
                                                request);

                        case EXCEL ->
                                generarExcelVacunasAplicadas(
                                                vacunaciones,
                                                request);
                };
        }

        private byte[] generarPdfVacunasAplicadas(
                        List<Vacunacion> vacunaciones,
                        ReporteRequest request) {

                try {

                        ByteArrayOutputStream salida = new ByteArrayOutputStream();

                        Document documento = new Document();

                        PdfWriter.getInstance(documento, salida);

                        documento.open();

                        documento.add(
                                        new Paragraph("REPORTE DE VACUNAS APLICADAS"));

                        documento.add(
                                        new Paragraph(
                                                        "Desde: " +
                                                                        request.getFechaDesde() +
                                                                        " - Hasta: " +
                                                                        request.getFechaHasta()));

                        documento.add(
                                        new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(5);

                        tabla.setWidthPercentage(100);

                        tabla.addCell(new PdfPCell(
                                        new Phrase("Fecha")));

                        tabla.addCell(new PdfPCell(
                                        new Phrase("Ciudadano")));

                        tabla.addCell(new PdfPCell(
                                        new Phrase("Vacuna")));

                        tabla.addCell(new PdfPCell(
                                        new Phrase("Dosis")));

                        tabla.addCell(new PdfPCell(
                                        new Phrase("Personal de Salud")));

                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        for (Vacunacion vacunacion : vacunaciones) {

                                tabla.addCell(
                                                vacunacion.getFechaAplicacion()
                                                                .format(formatoFecha));

                                tabla.addCell(
                                                obtenerNombreCiudadano(vacunacion));

                                tabla.addCell(
                                                obtenerNombreVacuna(vacunacion));

                                tabla.addCell(
                                                String.valueOf(
                                                                vacunacion.getDosis()));

                                tabla.addCell(
                                                obtenerNombrePersonal(vacunacion));
                        }

                        documento.add(tabla);

                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException e) {

                        throw new RuntimeException(
                                        "Error al generar el PDF de vacunas aplicadas",
                                        e);
                }
        }

        private byte[] generarExcelVacunasAplicadas(
                        List<Vacunacion> vacunaciones,
                        ReporteRequest request) {

                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        Sheet hoja = workbook.createSheet("Vacunas Aplicadas");

                        // Título
                        Row filaTitulo = hoja.createRow(0);

                        filaTitulo.createCell(0)
                                        .setCellValue("REPORTE DE VACUNAS APLICADAS");

                        // Fechas
                        Row filaFechas = hoja.createRow(1);

                        filaFechas.createCell(0)
                                        .setCellValue(
                                                        "Desde: " +
                                                                        request.getFechaDesde() +
                                                                        " - Hasta: " +
                                                                        request.getFechaHasta());

                        // Encabezados
                        Row encabezado = hoja.createRow(3);

                        encabezado.createCell(0)
                                        .setCellValue("Fecha");

                        encabezado.createCell(1)
                                        .setCellValue("Ciudadano");

                        encabezado.createCell(2)
                                        .setCellValue("Vacuna");

                        encabezado.createCell(3)
                                        .setCellValue("Dosis");

                        encabezado.createCell(4)
                                        .setCellValue("Personal de Salud");

                        // Datos
                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        int numeroFila = 4;

                        for (Vacunacion vacunacion : vacunaciones) {

                                Row fila = hoja.createRow(numeroFila++);

                                fila.createCell(0)
                                                .setCellValue(
                                                                vacunacion.getFechaAplicacion()
                                                                                .format(formatoFecha));

                                fila.createCell(1)
                                                .setCellValue(
                                                                obtenerNombreCiudadano(vacunacion));

                                fila.createCell(2)
                                                .setCellValue(
                                                                obtenerNombreVacuna(vacunacion));

                                fila.createCell(3)
                                                .setCellValue(
                                                                String.valueOf(vacunacion.getDosis()));

                                fila.createCell(4)
                                                .setCellValue(
                                                                obtenerNombrePersonal(vacunacion));
                        }

                        // Ajustar columnas
                        for (int i = 0; i < 5; i++) {
                                hoja.autoSizeColumn(i);
                        }

                        workbook.write(salida);

                        return salida.toByteArray();

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Error al generar el Excel de vacunas aplicadas",
                                        e);
                }
        }

        private String obtenerNombreCiudadano(
                        Vacunacion vacunacion) {

                if (vacunacion.getCiudadano() == null) {
                        return "No disponible";
                }

                return vacunacion.getCiudadano().getNombre();
        }

        private String obtenerNombreVacuna(
                        Vacunacion vacunacion) {

                if (vacunacion.getInventario() == null ||
                                vacunacion.getInventario().getVacuna() == null) {

                        return "No disponible";
                }

                return vacunacion.getInventario()
                                .getVacuna()
                                .getNombre();
        }

        private String obtenerNombrePersonal(
                        Vacunacion vacunacion) {

                if (vacunacion.getPersonalSalud() == null) {
                        return "No disponible";
                }

                return vacunacion.getPersonalSalud().getNombre();
        }

        private byte[] generarReporteInventarioLotes(
                        ReporteRequest request) {

                throw new UnsupportedOperationException(
                                "Reporte de inventario aún no implementado");
        }

        private byte[] generarReporteCoberturaCiudadanos(
                        ReporteRequest request) {

                throw new UnsupportedOperationException(
                                "Reporte de cobertura aún no implementado");
        }

        private void validarFechas(
                        ReporteRequest request) {

                if (request.getFechaDesde() == null ||
                                request.getFechaHasta() == null) {

                        throw new IllegalArgumentException(
                                        "Las fechas son obligatorias");
                }

                if (request.getFechaDesde()
                                .isAfter(request.getFechaHasta())) {

                        throw new IllegalArgumentException(
                                        "La fecha desde no puede ser posterior " +
                                                        "a la fecha hasta");
                }
        }
}