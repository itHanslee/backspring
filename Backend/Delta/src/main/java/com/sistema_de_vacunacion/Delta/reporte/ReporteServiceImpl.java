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
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLote;
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLoteRepository;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;

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
        private final InventarioLoteRepository inventarioLoteRepository;
        private final CiudadanoRepository ciudadanoRepository;

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

                List<InventarioLote> lotes = inventarioLoteRepository.findAll();

                return switch (request.getFormato()) {

                        case PDF ->
                                generarPdfInventarioLotes(
                                                lotes,
                                                request);

                        case EXCEL ->
                                generarExcelInventarioLotes(
                                                lotes,
                                                request);
                };
        }

        private byte[] generarPdfInventarioLotes(
                        List<InventarioLote> lotes,
                        ReporteRequest request) {

                try {

                        ByteArrayOutputStream salida = new ByteArrayOutputStream();

                        Document documento = new Document();

                        PdfWriter.getInstance(
                                        documento,
                                        salida);

                        documento.open();

                        documento.add(
                                        new Paragraph(
                                                        "REPORTE DE ESTADO DE INVENTARIO Y LOTES"));

                        documento.add(
                                        new Paragraph(
                                                        "Generado para el período: "
                                                                        + request.getFechaDesde()
                                                                        + " - "
                                                                        + request.getFechaHasta()));

                        documento.add(
                                        new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(6);

                        tabla.setWidthPercentage(100);

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Lote")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Vacuna")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Fabricante")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Stock")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Vencimiento")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Estado")));

                        for (InventarioLote lote : lotes) {

                                tabla.addCell(
                                                lote.getNumeroLote());

                                tabla.addCell(
                                                obtenerNombreVacunaLote(
                                                                lote));

                                tabla.addCell(
                                                obtenerFabricanteVacuna(
                                                                lote));

                                tabla.addCell(
                                                String.valueOf(
                                                                lote.getStockActual()));

                                tabla.addCell(
                                                String.valueOf(
                                                                lote.getFechaVencimiento()));

                                tabla.addCell(
                                                Boolean.TRUE.equals(
                                                                lote.getActivo())
                                                                                ? "ACTIVO"
                                                                                : "INACTIVO");
                        }

                        documento.add(tabla);

                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException e) {

                        throw new RuntimeException(
                                        "Error al generar el PDF de inventario y lotes",
                                        e);
                }
        }

        private byte[] generarExcelInventarioLotes(
                        List<InventarioLote> lotes,
                        ReporteRequest request) {

                try (
                                Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        Sheet hoja = workbook.createSheet(
                                        "Inventario y Lotes");

                        Row filaTitulo = hoja.createRow(0);

                        filaTitulo.createCell(0)
                                        .setCellValue(
                                                        "REPORTE DE ESTADO DE INVENTARIO Y LOTES");

                        Row filaFechas = hoja.createRow(1);

                        filaFechas.createCell(0)
                                        .setCellValue(
                                                        "Período: "
                                                                        + request.getFechaDesde()
                                                                        + " - "
                                                                        + request.getFechaHasta());

                        Row encabezado = hoja.createRow(3);

                        encabezado.createCell(0)
                                        .setCellValue("Lote");

                        encabezado.createCell(1)
                                        .setCellValue("Vacuna");

                        encabezado.createCell(2)
                                        .setCellValue("Fabricante");

                        encabezado.createCell(3)
                                        .setCellValue("Stock");

                        encabezado.createCell(4)
                                        .setCellValue("Vencimiento");

                        encabezado.createCell(5)
                                        .setCellValue("Estado");

                        int numeroFila = 4;

                        for (InventarioLote lote : lotes) {

                                Row fila = hoja.createRow(numeroFila++);

                                fila.createCell(0)
                                                .setCellValue(
                                                                lote.getNumeroLote());

                                fila.createCell(1)
                                                .setCellValue(
                                                                obtenerNombreVacunaLote(
                                                                                lote));

                                fila.createCell(2)
                                                .setCellValue(
                                                                obtenerFabricanteVacuna(
                                                                                lote));

                                fila.createCell(3)
                                                .setCellValue(
                                                                String.valueOf(
                                                                                lote.getStockActual()));

                                fila.createCell(4)
                                                .setCellValue(
                                                                String.valueOf(
                                                                                lote.getFechaVencimiento()));

                                fila.createCell(5)
                                                .setCellValue(
                                                                Boolean.TRUE.equals(
                                                                                lote.getActivo())
                                                                                                ? "ACTIVO"
                                                                                                : "INACTIVO");
                        }

                        for (int i = 0; i < 6; i++) {
                                hoja.autoSizeColumn(i);
                        }

                        workbook.write(salida);

                        return salida.toByteArray();

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Error al generar el Excel de inventario y lotes",
                                        e);
                }
        }

        private String obtenerNombreVacunaLote(
                        InventarioLote lote) {

                if (lote.getVacuna() == null) {
                        return "No disponible";
                }

                return lote
                                .getVacuna()
                                .getNombre();
        }

        private String obtenerFabricanteVacuna(
                        InventarioLote lote) {

                if (lote.getVacuna() == null) {
                        return "No disponible";
                }

                return lote
                                .getVacuna()
                                .getFabricante();
        }

        private byte[] generarReporteCoberturaCiudadanos(
                        ReporteRequest request) {

                List<Ciudadano> ciudadanos = ciudadanoRepository.findAll();

                return switch (request.getFormato()) {

                        case PDF ->
                                generarPdfCoberturaCiudadanos(
                                                ciudadanos,
                                                request);

                        case EXCEL ->
                                generarExcelCoberturaCiudadanos(
                                                ciudadanos,
                                                request);
                };
        }

        private byte[] generarPdfCoberturaCiudadanos(
                        List<Ciudadano> ciudadanos,
                        ReporteRequest request) {

                try {

                        ByteArrayOutputStream salida = new ByteArrayOutputStream();

                        Document documento = new Document();

                        PdfWriter.getInstance(
                                        documento,
                                        salida);

                        documento.open();

                        documento.add(
                                        new Paragraph(
                                                        "REPORTE DE CIUDADANOS REGISTRADOS"));

                        documento.add(
                                        new Paragraph(
                                                        "Generado para el período: "
                                                                        + request.getFechaDesde()
                                                                        + " - "
                                                                        + request.getFechaHasta()));

                        documento.add(
                                        new Paragraph(" "));

                        PdfPTable tabla = new PdfPTable(10);

                        tabla.setWidthPercentage(100);

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Tipo Documento")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Documento")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Nombre")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Apellido")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Correo")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Teléfono")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Estado")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Fecha Nacimiento")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Género")));

                        tabla.addCell(
                                        new PdfPCell(
                                                        new Phrase("Dirección")));

                        for (Ciudadano ciudadano : ciudadanos) {

                                tabla.addCell(
                                                String.valueOf(
                                                                ciudadano.getTipoDocumento()));

                                tabla.addCell(
                                                ciudadano.getNumeroDocumento());

                                tabla.addCell(
                                                ciudadano.getNombre());

                                tabla.addCell(
                                                ciudadano.getApellido());

                                tabla.addCell(
                                                ciudadano.getEmail());

                                tabla.addCell(
                                                ciudadano.getTelefono());

                                tabla.addCell(
                                                String.valueOf(
                                                                ciudadano.getEstado()));

                                tabla.addCell(
                                                String.valueOf(
                                                                ciudadano.getFechaNacimiento()));

                                tabla.addCell(
                                                String.valueOf(
                                                                ciudadano.getGenero()));

                                tabla.addCell(
                                                ciudadano.getDireccion());
                        }

                        documento.add(tabla);

                        documento.close();

                        return salida.toByteArray();

                } catch (DocumentException e) {

                        throw new RuntimeException(
                                        "Error al generar el PDF de ciudadanos registrados",
                                        e);
                }
        }

        private byte[] generarExcelCoberturaCiudadanos(
                        List<Ciudadano> ciudadanos,
                        ReporteRequest request) {

                try (
                                Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

                        Sheet hoja = workbook.createSheet(
                                        "Ciudadanos Registrados");

                        Row filaTitulo = hoja.createRow(0);

                        filaTitulo.createCell(0)
                                        .setCellValue(
                                                        "REPORTE DE CIUDADANOS REGISTRADOS");

                        Row filaFechas = hoja.createRow(1);

                        filaFechas.createCell(0)
                                        .setCellValue(
                                                        "Generado para el período: "
                                                                        + request.getFechaDesde()
                                                                        + " - "
                                                                        + request.getFechaHasta());

                        Row encabezado = hoja.createRow(3);

                        encabezado.createCell(0)
                                        .setCellValue("Tipo Documento");

                        encabezado.createCell(1)
                                        .setCellValue("Documento");

                        encabezado.createCell(2)
                                        .setCellValue("Nombre");

                        encabezado.createCell(3)
                                        .setCellValue("Apellido");

                        encabezado.createCell(4)
                                        .setCellValue("Correo");

                        encabezado.createCell(5)
                                        .setCellValue("Teléfono");

                        encabezado.createCell(6)
                                        .setCellValue("Estado");

                        encabezado.createCell(7)
                                        .setCellValue("Fecha Nacimiento");

                        encabezado.createCell(8)
                                        .setCellValue("Género");

                        encabezado.createCell(9)
                                        .setCellValue("Dirección");

                        int numeroFila = 4;

                        for (Ciudadano ciudadano : ciudadanos) {

                                Row fila = hoja.createRow(numeroFila++);

                                fila.createCell(0)
                                                .setCellValue(
                                                                String.valueOf(
                                                                                ciudadano.getTipoDocumento()));

                                fila.createCell(1)
                                                .setCellValue(
                                                                ciudadano.getNumeroDocumento());

                                fila.createCell(2)
                                                .setCellValue(
                                                                ciudadano.getNombre());

                                fila.createCell(3)
                                                .setCellValue(
                                                                ciudadano.getApellido());

                                fila.createCell(4)
                                                .setCellValue(
                                                                ciudadano.getEmail());

                                fila.createCell(5)
                                                .setCellValue(
                                                                ciudadano.getTelefono());

                                fila.createCell(6)
                                                .setCellValue(
                                                                String.valueOf(
                                                                                ciudadano.getEstado()));

                                fila.createCell(7)
                                                .setCellValue(
                                                                String.valueOf(
                                                                                ciudadano.getFechaNacimiento()));

                                fila.createCell(8)
                                                .setCellValue(
                                                                String.valueOf(
                                                                                ciudadano.getGenero()));

                                fila.createCell(9)
                                                .setCellValue(
                                                                ciudadano.getDireccion());
                        }

                        for (int i = 0; i < 10; i++) {
                                hoja.autoSizeColumn(i);
                        }

                        workbook.write(salida);

                        return salida.toByteArray();

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Error al generar el Excel de ciudadanos registrados",
                                        e);
                }
        }

        

        private void validarFechas(
                        ReporteRequest request) {

                if (request.getFechaDesde() == null ||
                                request.getFechaHasta() == null) {

                        throw new IllegalArgumentException(
                                        "Las fechas son obligatorias");
                }

                if (request.getFechaDesde()
                                .isAfter(
                                                request.getFechaHasta())) {

                        throw new IllegalArgumentException(
                                        "La fecha desde no puede ser posterior "
                                                        + "a la fecha hasta");
                }
        }
}