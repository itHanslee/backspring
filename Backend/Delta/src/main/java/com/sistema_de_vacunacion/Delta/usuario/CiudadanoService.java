package com.sistema_de_vacunacion.Delta.usuario;


import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.Vacunacion;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;

    public UsuarioDTO obtenerPerfilCiudadano(Long idCiudadano) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));
        return mapearADTO(ciudadano);
    }

    // Método para generar la estructura del carné digital en formato DTO/PDF
    

    public byte[] generarCarneVacunacionPDF(Long idCiudadano) {

        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        try {
		ByteArrayOutputStream salida = new ByteArrayOutputStream();

            	Document documento = new Document(
                    PageSize.A4.rotate(),
                    28,
                    28,
                    25,
                    25
            );

            PdfWriter.getInstance(documento, salida);

            documento.open();

            // COLORES

            java.awt.Color verdeDelta = new java.awt.Color(34, 145, 125);
            java.awt.Color verdeOscuro = new java.awt.Color(31, 88, 75);
	        java.awt.Color grisClaro = new java.awt.Color(225, 231, 229);
            java.awt.Color grisTexto = new java.awt.Color(80, 80, 80);

            // FUENTES


            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17, verdeOscuro);
            Font subtitulo = FontFactory.getFont( FontFactory.HELVETICA_BOLD, 8, verdeDelta);
            Font etiqueta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, grisTexto);
            Font valor = FontFactory.getFont(FontFactory.HELVETICA, 9, java.awt.Color.BLACK);
            Font encabezadoTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, java.awt.Color.WHITE);
            Font contenidoTabla = FontFactory.getFont( FontFactory.HELVETICA, 7, java.awt.Color.BLACK);

            // ENCABEZADO

            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{ 70f, 30f});
            PdfPCell tituloCelda = new PdfPCell();
            tituloCelda.setBorder(Rectangle.NO_BORDER);
            Paragraph tituloDelta =
                    new Paragraph(
                            "DELTA",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    18,
                                    verdeOscuro
                            )
                    );

            tituloDelta.setSpacingAfter(2);

            tituloCelda.addElement(tituloDelta);

            Paragraph tituloCertificado =
                    new Paragraph(
                            "VACCINATION CERTIFICATE - DELTA",
                            titulo
                    );

            tituloCelda.addElement(tituloCertificado);

            encabezado.addCell(tituloCelda);

            PdfPCell autenticacion =
                    new PdfPCell();

            autenticacion.setBorder(Rectangle.NO_BORDER);
            autenticacion.setHorizontalAlignment(Element.ALIGN_RIGHT);

            autenticacion.addElement(
                    new Paragraph(
                            "ORD. DE AUTENTICACIÓN",
                            etiqueta
                    )
            );

            autenticacion.addElement(
                    new Paragraph(
                            "NO REGISTRA",
                            valor
                    )
            );

            encabezado.addCell(autenticacion);

            documento.add(encabezado);

            documento.add(
                    new Paragraph(" ")
            );

            // INFORMACIÓN DEL CIUDADANO


            PdfPTable datosCiudadano =new PdfPTable(4);
            datosCiudadano.setWidthPercentage(100);
            datosCiudadano.setWidths(new float[]{ 25f, 25f, 25f, 25f});

            agregarCampo(
                    datosCiudadano,
                    "Tipo de identificación / ID Type",
                    String.valueOf(
                            ciudadano.getTipoDocumento()
                    ),
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "Número de identificación / ID Number",
                    ciudadano.getNumeroDocumento(),
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "Fecha de nacimiento / Date of birth",
                    ciudadano.getFechaNacimiento() != null
                            ? ciudadano.getFechaNacimiento()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy"
                                    )
                            )
                            : "NO REGISTRA",
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "País de nacimiento / Country of birth",
                    "NO REGISTRA",
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "Correo electrónico / E-mail",
                    ciudadano.getEmail(),
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "Nombre completo / Full name",
                    ciudadano.getNombre()
                            + " "
                            + ciudadano.getApellido(),
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "País / Country",
                    "COLOMBIA",
                    grisClaro,
                    etiqueta,
                    valor
            );

            agregarCampo(
                    datosCiudadano,
                    "Estado / Status",
                    ciudadano.getEstado() != null
                            ? String.valueOf(
                                    ciudadano.getEstado()
                            )
                            : "NO REGISTRA",
                    grisClaro,
                    etiqueta,
                    valor
            );

            documento.add(datosCiudadano);

            documento.add(
                    new Paragraph(" ")
            );

            // TÍTULO DE LA TABLA

            Paragraph detalle =
                    new Paragraph(
                            "Datos de Vacunación / Vaccination detail",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    10,
                                    verdeOscuro
                            )
                    );

            detalle.setSpacingAfter(5);

            documento.add(detalle);

            // TABLA DE VACUNACIÓN

            PdfPTable tabla =
                    new PdfPTable(7);

            tabla.setWidthPercentage(100);

            tabla.setWidths(new float[]{ 18f, 10f, 14f, 18f, 14f, 13f, 19f});

            agregarEncabezado(
                    tabla,
                    "Vacuna",
                    encabezadoTabla,
                    verdeDelta
            );

            agregarEncabezado(
                    tabla,
                    "Dosis",
                    encabezadoTabla,
                    verdeDelta
            );

            agregarEncabezado(
                    tabla,
                    "Fecha de aplicación",
                    encabezadoTabla,
                    verdeDelta
            );

            agregarEncabezado(
                    tabla,
                    "Laboratorio",
                    encabezadoTabla,
                    verdeDelta
            );

            agregarEncabezado(
                    tabla,
                    "Número de lote",
                    encabezadoTabla,
                    verdeDelta
            );

            agregarEncabezado(
                    tabla,
                    "IPS vacunadora",
                    encabezadoTabla,
                    verdeDelta
            );

            agregarEncabezado(
                    tabla,
                    "Nombres y apellidos del vacunador",
                    encabezadoTabla,
                    verdeDelta
            );

            List<Vacunacion> vacunaciones =
                    ciudadano.getVacunaciones();

            if (vacunaciones != null) {

                vacunaciones.stream()
                        .sorted(
                                Comparator.comparing(
                                        Vacunacion::getFechaAplicacion,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                        )
                        .forEach(vacunacion -> {

                            String nombreVacuna =
                                    "NO REGISTRA";

                            String fabricante =
                                    "NO REGISTRA";

                            String lote =
                                    "NO REGISTRA";

                            if (
                                    vacunacion.getInventario() != null
                                    &&
                                    vacunacion
                                            .getInventario()
                                            .getVacuna() != null
                            ) {

                                nombreVacuna =
                                        vacunacion
                                                .getInventario()
                                                .getVacuna()
                                                .getNombre();

                                fabricante =
                                        vacunacion
                                                .getInventario()
                                                .getVacuna()
                                                .getFabricante();

                                lote =
                                        vacunacion
                                                .getInventario()
                                                .getNumeroLote();
                            }

                            String vacunador =
                                    "NO REGISTRA";

                            if (
                                    vacunacion
                                            .getPersonalSalud() != null
                            ) {

                                vacunador =
                                        vacunacion
                                                .getPersonalSalud()
                                                .getNombre()
                                                + " "
                                                + vacunacion
                                                .getPersonalSalud()
                                                .getApellido();
                            }

                            tabla.addCell(
                                    crearCelda(
                                            nombreVacuna,
                                            contenidoTabla
                                    )
                            );

                            tabla.addCell(
                                    crearCelda(
                                            String.valueOf(
                                                    vacunacion.getDosis()
                                            ),
                                            contenidoTabla
                                    )
                            );

                            tabla.addCell(
                                    crearCelda(
                                            vacunacion
                                                    .getFechaAplicacion()
                                                    .format(
                                                            DateTimeFormatter
                                                                    .ofPattern(
                                                                            "dd/MM/yyyy"
                                                                    )
                                                    ),
                                            contenidoTabla
                                    )
                            );

                            tabla.addCell(
                                    crearCelda(
                                            fabricante,
                                            contenidoTabla
                                    )
                            );

                            tabla.addCell(
                                    crearCelda(
                                            lote,
                                            contenidoTabla
                                    )
                            );

                            tabla.addCell(
                                    crearCelda(
                                            "DELTA",
                                            contenidoTabla
                                    )
                            );

                            tabla.addCell(
                                    crearCelda(
                                            vacunador,
                                            contenidoTabla
                                    )
                            );
                        });
            }

            documento.add(tabla);

            documento.add(
                    new Paragraph(" ")
            );

            Paragraph pie =
                    new Paragraph(
                            "Documento generado digitalmente por el sistema de vacunación DELTA.",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA,
                                    7,
                                    grisTexto
                            )
                    );

            pie.setAlignment(Element.ALIGN_CENTER);

            documento.add(pie);

            documento.close();

            return salida.toByteArray();

        } catch (DocumentException e) {

            throw new RuntimeException(
                    "Error al generar el carnet de vacunación PDF",
                    e
            );
        }
    }

    private void agregarCampo(
            PdfPTable tabla,
            String etiquetaTexto,
            String valorTexto,
            java.awt.Color fondo,
            Font etiqueta,
            Font valor
    ) {

        PdfPCell celda =
                new PdfPCell();

        celda.setPadding(5);
        celda.setBackgroundColor(
                new java.awt.Color(
                        247,
                        249,
                        248
                )
        );

        Paragraph p =
                new Paragraph();

        p.add(
                new Chunk(
                        etiquetaTexto + "\n",
                        etiqueta
                )
        );

        p.add(
                new Chunk(
                        valorTexto != null
                                ? valorTexto
                                : "NO REGISTRA",
                        valor
                )
        );

        celda.addElement(p);

        tabla.addCell(celda);
    }

    private void agregarEncabezado(
            PdfPTable tabla,
            String texto,
            Font fuente,
            java.awt.Color color
    ) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setBackgroundColor(color);
        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );
        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );
        celda.setPadding(5);

        tabla.addCell(celda);
    }

    private PdfPCell crearCelda(
            String texto,
            Font fuente
    ) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto != null
                                        ? texto
                                        : "NO REGISTRA",
                                fuente
                        )
                );

        celda.setPadding(4);
        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        return celda;
    }



    private UsuarioDTO mapearADTO(Ciudadano c) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setEmail(c.getEmail());
        dto.setNumeroDocumento(c.getNumeroDocumento());
        dto.setTipoUsuario(c.getPermisos());
        return dto;
    }
}