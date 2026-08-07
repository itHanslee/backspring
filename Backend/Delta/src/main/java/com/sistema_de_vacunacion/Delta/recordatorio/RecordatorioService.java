package com.sistema_de_vacunacion.Delta.recordatorio;

import java.util.List;

import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;

public interface RecordatorioService {

    RecordatorioDTO crearRecordatorio(RecordatorioDTO dto);

    RecordatorioDTO obtenerPorId(Integer id);

    List<RecordatorioDTO> listarTodos();

    List<RecordatorioDTO> buscarPorEstado(String estado);

    void marcarComoEnviado(Integer id);
}