package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;
import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacionRepository;
import com.sistema_de_vacunacion.Delta.vacuna.Vacuna;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sistema_de_vacunacion.Delta.vacunacion.Vacunacion;
import com.sistema_de_vacunacion.Delta.vacunacion.VacunacionRepository;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final EsquemaVacunacionRepository esquemaRepository;
    private final VacunacionRepository vacunacionRepository;

    public RecordatorioServiceImpl(RecordatorioRepository recordatorioRepository,
            CiudadanoRepository ciudadanoRepository,
            EsquemaVacunacionRepository esquemaRepository,
            VacunacionRepository vacunacionRepository) {
        this.recordatorioRepository = recordatorioRepository;
        this.ciudadanoRepository = ciudadanoRepository;
        this.esquemaRepository = esquemaRepository;
        this.vacunacionRepository = vacunacionRepository;
    }

    @Override
    @Transactional
    public RecordatorioDTO crearRecordatorio(RecordatorioDTO dto) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dto.getIdCiudadano())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        EsquemaVacunacion esquema = esquemaRepository.findById(dto.getIdEsquema())
                .orElseThrow(() -> new RecursoNoEncontradoException("Esquema de vacunación no encontrado"));

        Recordatorio recordatorio = Recordatorio.builder()
                .ciudadano(ciudadano)
                .esquema(esquema)
                .fechaProgramada(dto.getFechaProgramada())
                .fechaEnvio(dto.getFechaEnvio())
                .mensaje(dto.getMensaje())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoRecordatorio.Pendiente)
                .build();

        return mapToDTO(recordatorioRepository.save(recordatorio));
    }

    @Override
    @Transactional(readOnly = true)
    public RecordatorioDTO obtenerPorId(Integer id) {
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recordatorio no encontrado"));
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
    public List<RecordatorioDTO> buscarPorEstado(EstadoRecordatorio estado) {
        return recordatorioRepository.findByEstado(estado)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarComoEnviado(Integer id) {
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recordatorio no encontrado"));

        recordatorio.setEstado(EstadoRecordatorio.Enviado);
        recordatorio.setFechaEnvio(LocalDateTime.now());

        recordatorioRepository.save(recordatorio);
    }

    @Override
    @Transactional
    public List<RecordatorioDTO> generarRecordatorios(Long idCiudadano) {

        

        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        List<Vacunacion> vacunaciones = vacunacionRepository.findByCiudadanoOrderByFechaAplicacionAsc(ciudadano);

        for (Vacunacion vacunacion : vacunaciones) {

            if (vacunacion.getInventario() != null &&
                    vacunacion.getInventario().getVacuna() != null) {

                Vacuna vacuna = vacunacion.getInventario().getVacuna();

                List<EsquemaVacunacion> esquemas = esquemaRepository
                        .findByVacunaIdOrderByDosisNumeroAsc(vacuna.getId());

                System.out.println("Vacuna aplicada: " + vacuna.getNombre());

                for (EsquemaVacunacion esquema : esquemas) {

                    int dosisEsperada;

                    switch (esquema.getDosisNumero()) {
                        case Primera -> dosisEsperada = 1;
                        case Segunda -> dosisEsperada = 2;
                        case Tercera -> dosisEsperada = 3;
                        case Unica -> dosisEsperada = 1;
                        default -> {
                            continue;
                        }
                    }

                    boolean dosisAplicada = vacunaciones.stream()
                            .anyMatch(v -> v.getInventario() != null &&
                                    v.getInventario().getVacuna() != null &&
                                    v.getInventario().getVacuna().getId().equals(vacuna.getId()) &&
                                    v.getDosis() != null &&
                                    v.getDosis() == dosisEsperada);

                    System.out.println(
                            vacuna.getNombre()
                                    + " → "
                                    + esquema.getDosisNumero()
                                    + " → aplicada: "
                                    + dosisAplicada);
                }
            }
        }

        return List.of();
    }
    private int obtenerOrdenDosis(EsquemaVacunacion esquema) {

        
        

    return switch (esquema.getDosisNumero()) {
        case Unica -> 1;
        case Primera -> 1;
        case Segunda -> 2;
        case Tercera -> 3;
        case Refuerzo -> 4;
    };
}

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
        }

        return dto;
    }
}