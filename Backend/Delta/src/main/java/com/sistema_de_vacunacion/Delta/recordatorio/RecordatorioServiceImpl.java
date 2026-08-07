package com.sistema_de_vacunacion.Delta.recordatorio;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;
import com.sistema_de_vacunacion.Delta.usuario.Usuario;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;

@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RecordatorioServiceImpl(RecordatorioRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    @Override
    @Transactional
    public RecordatorioDTO crearRecordatorio(RecordatorioDTO dto) {
        Recordatorio recordatorio = mapToEntity(dto);
        return mapToDTO(recordatorioRepository.save(recordatorio));
    }

    @Override
    @Transactional(readOnly = true)
    public RecordatorioDTO obtenerPorId(Integer id) {
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado con ID: " + id));
        return mapToDTO(recordatorio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarTodos() {
        return recordatorioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> buscarPorEstado(String estado) {
        return recordatorioRepository.findByEstado(estado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarComoEnviado(Integer id) {
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado con ID: " + id));
        recordatorio.marcarComoEnviado();
        recordatorioRepository.save(recordatorio);
    }

    private RecordatorioDTO mapToDTO(Recordatorio entity) {
        RecordatorioDTO dto = new RecordatorioDTO();
        dto.setId(entity.getId());
        dto.setFechaProgramada(entity.getFechaProgramada());
        dto.setFechaEnvio(entity.getFechaEnvio());
        dto.setMensaje(entity.getMensaje());
        dto.setEstado(entity.getEstado());

        if (entity.getUsuario() != null) {
            dto.setIdUsuario(entity.getUsuario().getId());
        }
        if (entity.getEsquema() != null) {
            dto.setIdEsquema(entity.getEsquema().getId());
        }

        return dto;
    }

    private Recordatorio mapToEntity(RecordatorioDTO dto) {
        Recordatorio entity = new Recordatorio();
        entity.setId(dto.getId());
        entity.setFechaProgramada(dto.getFechaProgramada());
        entity.setFechaEnvio(dto.getFechaEnvio());
        entity.setMensaje(dto.getMensaje());
        entity.setEstado(dto.getEstado());

        // getReference = crea un proxy sin hacer SELECT a la BD (sirve para clases abstractas)
        if (dto.getIdUsuario() != null) {
            Usuario usuario = entityManager.getReference(Usuario.class, dto.getIdUsuario());
            entity.setUsuario(usuario);
        }

        if (dto.getIdEsquema() != null) {
            EsquemaVacunacion esquema = entityManager.getReference(EsquemaVacunacion.class, dto.getIdEsquema());
            entity.setEsquema(esquema);
        }

        return entity;
    }
}