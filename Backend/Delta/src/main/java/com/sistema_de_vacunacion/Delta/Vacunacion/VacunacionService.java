package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.vacunacion.dto.RegistrarVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunacionResponseDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunaPendienteDTO;

import java.util.List;

public interface VacunacionService {

    void registrarAplicacion(
            RegistrarVacunacionDTO dto,
            String emailPersonalSalud
    );

    List<VacunacionResponseDTO> obtenerVacunasAplicadas(
            Long idCiudadano
    );

    List<VacunacionResponseDTO> obtenerHistorial(
            Long idCiudadano
    );

    List<VacunaPendienteDTO> obtenerVacunasPendientes(
            Long idCiudadano
    );
}