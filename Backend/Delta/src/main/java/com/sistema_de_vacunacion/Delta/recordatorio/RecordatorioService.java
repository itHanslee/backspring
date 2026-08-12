package com.sistema_de_vacunacion.Delta.recordatorio;

import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;
import com.sistema_de_vacunacion.Delta.recordatorio.enums.EstadoRecordatorio;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface RecordatorioService {

    RecordatorioDTO crearRecordatorio(RecordatorioDTO dto);

    RecordatorioDTO obtenerPorId(Integer id);

    List<RecordatorioDTO> listarTodos();

    List<RecordatorioDTO> buscarPorEstado(EstadoRecordatorio estado);

    void marcarComoEnviado(Integer id);
}