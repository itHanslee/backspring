package com.vacunacion.vacunacion;

import com.vacunacion.vacunacion.dto.VacunacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacunacionServiceImpl implements VacunacionService {

    private final VacunacionRepository vacunacionRepository;

    @Override
    public VacunacionDTO registrar(VacunacionDTO dto) {
        // TODO: verificar esquema, descontar inventario, registrar auditoría
        return null;
    }

    @Override
    public List<VacunacionDTO> historialPorCiudadano(Long ciudadanoId) {
        // TODO
        return null;
    }

    @Override
    public VacunacionDTO buscarPorId(Long id) {
        // TODO
        return null;
    }
}
