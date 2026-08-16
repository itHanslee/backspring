package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;

import com.sistema_de_vacunacion.Delta.vacuna.dto.EsquemaVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.InventarioLoteDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.VacunaDTO;

public interface VacunaService {

    //vacunas
    VacunaDTO crearVacuna(VacunaDTO dto);
    VacunaDTO obtenerVacunaPorId(Integer id);
    List<VacunaDTO> listarTodasLasVacunas();
    VacunaDTO actualizarVacuna(Integer id, VacunaDTO dto);
    void cambiarEstadoVacuna(Integer id, String estado);
    //lotes
    InventarioLoteDTO registrarLote(InventarioLoteDTO dto);
    List<InventarioLoteDTO> listarLotesPorVacuna(Integer idVacuna);
    List<InventarioLoteDTO> listarTodosLosLotesPorVacuna(Integer idVacuna);
    boolean descontarStockLote(Integer idLote, Integer cantidad);
    void actualizarLotesVencidos();
    //esquemas
    EsquemaVacunacionDTO registrarEsquema (EsquemaVacunacionDTO dto);
    List<EsquemaVacunacionDTO> obtenerEsquemasPorVacuna(Integer idVacuna);
    void eliminarEsquema(Integer idEsquema);
}