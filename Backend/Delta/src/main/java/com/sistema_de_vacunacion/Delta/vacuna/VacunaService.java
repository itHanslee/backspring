package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;

import com.sistema_de_vacunacion.Delta.vacuna.dto.EsquemaVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.InventarioLoteDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.VacunaDTO;

public interface VacunaService {
    VacunaDTO crearVacuna(VacunaDTO dto);
    VacunaDTO obtenerVacunaPorId(Integer id);
    List<VacunaDTO> listarTodasLasVacunas();
    VacunaDTO actualizarVacuna(Integer id, VacunaDTO dto);
    void cambiarEstadoVacuna(Integer id, String estado);

    InventarioLoteDTO registrarLote(InventarioLoteDTO dto);
    List<InventarioLoteDTO> listarLotesPorVacuna(Integer idVacuna);
    boolean descontarStockLote(Integer idLote, Integer cantidad);

    EsquemaVacunacionDTO registrarEsquema (EsquemaVacunacionDTO dto);
    List<EsquemaVacunacionDTO> obtenerEsquemasPorVacuna(Integer idVacuna);
    void eliminarEsquema(Integer idEsquema);
}