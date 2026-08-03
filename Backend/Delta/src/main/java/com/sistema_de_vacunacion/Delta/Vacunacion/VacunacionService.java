package com.vacunacion.vacunacion;

import com.vacunacion.vacunacion.dto.VacunacionDTO;

import java.util.List;

public interface VacunacionService {
    VacunacionDTO registrar(VacunacionDTO dto);
    List<VacunacionDTO> historialPorCiudadano(Long ciudadanoId);
    VacunacionDTO buscarPorId(Long id);
}
