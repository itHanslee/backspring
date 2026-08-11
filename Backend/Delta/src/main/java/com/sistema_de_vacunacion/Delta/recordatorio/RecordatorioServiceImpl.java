package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;
import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final EsquemaVacunacionRepository esquemaRepository;

    public RecordatorioServiceImpl(RecordatorioRepository recordatorioRepository,
                                   CiudadanoRepository ciudadanoRepository,
                                   EsquemaVacunacionRepository esquemaRepository) {
        this.recordatorioRepository = recordatorioRepository;
        this.ciudadanoRepository = ciudadanoRepository;
        this.esquemaRepository = esquemaRepository;
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