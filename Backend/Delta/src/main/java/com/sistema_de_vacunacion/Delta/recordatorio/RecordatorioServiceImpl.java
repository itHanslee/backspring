package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;
import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacionRepository;
import com.sistema_de_vacunacion.Delta.vacuna.Vacuna;
import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;
import com.sistema_de_vacunacion.Delta.vacuna.strategy.ServicioCalculadorEsquema;
import com.sistema_de_vacunacion.Delta.vacunacion.Vacunacion;
import com.sistema_de_vacunacion.Delta.vacunacion.VacunacionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final EsquemaVacunacionRepository esquemaRepository;
    private final VacunacionRepository vacunacionRepository;
    private final ServicioCalculadorEsquema calculadorEsquema;

    public RecordatorioServiceImpl(
            RecordatorioRepository recordatorioRepository,
            CiudadanoRepository ciudadanoRepository,
            EsquemaVacunacionRepository esquemaRepository,
            VacunacionRepository vacunacionRepository,
            ServicioCalculadorEsquema calculadorEsquema) {

        this.recordatorioRepository = recordatorioRepository;
        this.ciudadanoRepository = ciudadanoRepository;
        this.esquemaRepository = esquemaRepository;
        this.vacunacionRepository = vacunacionRepository;
        this.calculadorEsquema = calculadorEsquema;
    }

    @Override
    @Transactional
    public RecordatorioDTO crearRecordatorio(RecordatorioDTO dto) {

        Ciudadano ciudadano = ciudadanoRepository.findById(dto.getIdCiudadano())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        EsquemaVacunacion esquema = esquemaRepository.findById(dto.getIdEsquema())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Esquema de vacunación no encontrado"));

        Recordatorio recordatorio = Recordatorio.builder()
                .ciudadano(ciudadano)
                .esquema(esquema)
                .fechaProgramada(dto.getFechaProgramada())
                .fechaEnvio(dto.getFechaEnvio())
                .mensaje(dto.getMensaje())
                .estado(dto.getEstado() != null
                        ? dto.getEstado()
                        : EstadoRecordatorio.Pendiente)
                .build();

        return mapToDTO(recordatorioRepository.save(recordatorio));
    }

    @Override
    @Transactional(readOnly = true)
    public RecordatorioDTO obtenerPorId(Integer id) {

        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Recordatorio no encontrado"));

        return mapToDTO(recordatorio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarTodos() {

        return recordatorioRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> buscarPorEstado(
            EstadoRecordatorio estado) {

        return recordatorioRepository.findByEstado(estado)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarComoEnviado(Integer id) {

        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Recordatorio no encontrado"));

        recordatorio.setEstado(EstadoRecordatorio.Enviado);
        recordatorio.setFechaEnvio(LocalDateTime.now());

        recordatorioRepository.save(recordatorio);
    }

    @Override
    @Transactional
    public List<RecordatorioDTO> generarRecordatorios(Long idCiudadano) {

        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ciudadano no encontrado"));

        List<Vacunacion> vacunaciones = vacunacionRepository
                .findByCiudadanoOrderByFechaAplicacionAsc(ciudadano);

        if (vacunaciones.isEmpty()) {
            return List.of();
        }

        List<Vacuna> vacunas = vacunaciones.stream()
                .filter(v -> v.getInventario() != null)
                .map(v -> v.getInventario().getVacuna())
                .filter(v -> v != null)
                .distinct()
                .collect(Collectors.toList());

        List<RecordatorioDTO> recordatoriosGenerados = new ArrayList<>();

        for (Vacuna vacuna : vacunas) {

            List<EsquemaVacunacion> esquemas = esquemaRepository
                    .findByVacunaIdOrderByDosisNumeroAsc(
                            vacuna.getId());

            if (esquemas.isEmpty()) {
                continue;
            }

            EsquemaVacunacion siguienteEsquema = buscarSiguienteDosisInicial(
                    vacunaciones,
                    vacuna,
                    esquemas);

            if (siguienteEsquema != null) {

                RecordatorioDTO recordatorio = generarRecordatorioDosisInicial(
                        ciudadano,
                        vacuna,
                        vacunaciones,
                        siguienteEsquema);

                if (recordatorio != null) {
                    recordatoriosGenerados.add(recordatorio);
                }

                continue;
            }

            EsquemaVacunacion esquemaRefuerzo = buscarEsquemaRefuerzo(esquemas);

            if (esquemaRefuerzo == null) {
                continue;
            }

            /*
             * Buscamos la última dosis que realmente
             * completa el esquema inicial.
             */
            LocalDateTime fechaUltimaDosisInicial = buscarFechaUltimaDosisInicial(
                    vacunaciones,
                    vacuna,
                    esquemas);

            if (fechaUltimaDosisInicial == null) {
                continue;
            }

            /*
             * Calculamos cuándo corresponde el refuerzo.
             */
            LocalDate fechaRefuerzo = calculadorEsquema.calcularProximaFecha(
                    esquemaRefuerzo,
                    ciudadano.getFechaNacimiento(),
                    fechaUltimaDosisInicial.toLocalDate());

            /*
             * El refuerzo todavía no corresponde.
             */
            if (LocalDate.now().isBefore(fechaRefuerzo)) {
                continue;
            }

            /*
             * Evitamos duplicar el recordatorio.
             */
            if (recordatorioRepository
                    .existsByCiudadanoIdAndEsquemaId(
                            ciudadano.getId(),
                            esquemaRefuerzo.getId())) {

                continue;
            }

            Recordatorio recordatorio = Recordatorio.builder()
                    .ciudadano(ciudadano)
                    .esquema(esquemaRefuerzo)
                    .fechaProgramada(
                            fechaRefuerzo.atStartOfDay())
                    .mensaje(
                            "Es momento de aplicar el refuerzo de "
                                    + vacuna.getNombre()
                                    + ". Solicite su cita.")
                    .estado(EstadoRecordatorio.Pendiente)
                    .build();

            Recordatorio guardado = recordatorioRepository.save(recordatorio);

            recordatoriosGenerados.add(
                    mapToDTO(guardado));
        }

        return recordatoriosGenerados;
    }

    private EsquemaVacunacion buscarSiguienteDosisInicial(
            List<Vacunacion> vacunaciones,
            Vacuna vacuna,
            List<EsquemaVacunacion> esquemas) {

        NumeroDosis[] ordenInicial = {
                NumeroDosis.Unica,
                NumeroDosis.Primera,
                NumeroDosis.Segunda,
                NumeroDosis.Tercera
        };

        for (NumeroDosis dosis : ordenInicial) {

            for (EsquemaVacunacion esquema : esquemas) {

                if (esquema.getDosisNumero() == dosis) {

                    if (!dosisYaAplicada(
                            vacunaciones,
                            vacuna,
                            dosis)) {

                        return esquema;
                    }

                    break;
                }
            }
        }

        return null;
    }

    private EsquemaVacunacion buscarEsquemaRefuerzo(
            List<EsquemaVacunacion> esquemas) {

        return esquemas.stream()
                .filter(esquema -> esquema.getDosisNumero() == NumeroDosis.Refuerzo)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime buscarFechaUltimaDosisInicial(
            List<Vacunacion> vacunaciones,
            Vacuna vacuna,
            List<EsquemaVacunacion> esquemas) {

        NumeroDosis[] ordenInicial = {
                NumeroDosis.Tercera,
                NumeroDosis.Segunda,
                NumeroDosis.Primera,
                NumeroDosis.Unica
        };

        for (NumeroDosis dosis : ordenInicial) {

            boolean existeEnEsquema = esquemas.stream()
                    .anyMatch(esquema -> esquema.getDosisNumero() == dosis);

            if (!existeEnEsquema) {
                continue;
            }

            LocalDateTime fecha = vacunaciones.stream()
                    .filter(v -> v.getInventario() != null)
                    .filter(v -> v.getInventario().getVacuna() != null)
                    .filter(v -> v.getInventario()
                            .getVacuna()
                            .getId()
                            .equals(vacuna.getId()))
                    .filter(v -> v.getDosis() == dosis)
                    .map(Vacunacion::getFechaAplicacion)
                    .filter(fechaAplicacion -> fechaAplicacion != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            if (fecha != null) {
                return fecha;
            }
        }

        return null;
    }

    /*
     * =============================================================
     * COMPROBAR SI UNA DOSIS YA FUE APLICADA
     * =============================================================
     */
    private boolean dosisYaAplicada(
            List<Vacunacion> vacunaciones,
            Vacuna vacuna,
            NumeroDosis dosis) {

        return vacunaciones.stream()
                .anyMatch(v -> v.getInventario() != null
                        && v.getInventario().getVacuna() != null
                        && v.getInventario()
                                .getVacuna()
                                .getId()
                                .equals(vacuna.getId())
                        && v.getDosis() == dosis);
    }

    /*
     * =============================================================
     * GENERAR RECORDATORIO DE DOSIS INICIAL
     * =============================================================
     */
    private RecordatorioDTO generarRecordatorioDosisInicial(
            Ciudadano ciudadano,
            Vacuna vacuna,
            List<Vacunacion> vacunaciones,
            EsquemaVacunacion esquema) {

        if (recordatorioRepository
                .existsByCiudadanoIdAndEsquemaId(
                        ciudadano.getId(),
                        esquema.getId())) {

            return null;
        }

        LocalDateTime fechaUltimaDosis = vacunaciones.stream()
                .filter(v -> v.getInventario() != null)
                .filter(v -> v.getInventario()
                        .getVacuna() != null)
                .filter(v -> v.getInventario()
                        .getVacuna()
                        .getId()
                        .equals(vacuna.getId()))
                .map(Vacunacion::getFechaAplicacion)
                .filter(fecha -> fecha != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDate fechaProxima = calculadorEsquema.calcularProximaFecha(
                esquema,
                ciudadano.getFechaNacimiento(),
                fechaUltimaDosis != null
                        ? fechaUltimaDosis.toLocalDate()
                        : null);

        Recordatorio recordatorio = Recordatorio.builder()
                .ciudadano(ciudadano)
                .esquema(esquema)
                .fechaProgramada(
                        fechaProxima.atStartOfDay())
                .mensaje(
                        "Recordatorio de vacunación: "
                                + "próxima dosis "
                                + esquema.getDosisNumero()
                                + " de "
                                + vacuna.getNombre())
                .estado(EstadoRecordatorio.Pendiente)
                .build();

        Recordatorio guardado = recordatorioRepository.save(recordatorio);

        return mapToDTO(guardado);
    }

    /*
     * =============================================================
     * MAPEAR ENTIDAD A DTO
     * =============================================================
     */
   private RecordatorioDTO mapToDTO(Recordatorio entity) {

    RecordatorioDTO dto = new RecordatorioDTO();

    dto.setId(entity.getId());
    dto.setFechaProgramada(entity.getFechaProgramada());
    dto.setFechaEnvio(entity.getFechaEnvio());
    dto.setMensaje(entity.getMensaje());
    dto.setEstado(entity.getEstado());

    if (entity.getCiudadano() != null) {
        dto.setIdCiudadano(entity.getCiudadano().getId());
    }

    if (entity.getEsquema() != null) {
        dto.setIdEsquema(entity.getEsquema().getId());

        if (entity.getEsquema().getVacuna() != null) {
            dto.setVacunaNombre(
                entity.getEsquema().getVacuna().getNombre()
            );
        }
    }

    return dto;
}
}