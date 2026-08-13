package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSalud;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSaludRepository;
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLote;
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLoteRepository;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.RegistrarVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunacionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacunacionServiceImpl implements VacunacionService {

    private final VacunacionRepository vacunacionRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final PersonalSaludRepository personalSaludRepository;
    private final InventarioLoteRepository inventarioLoteRepository;

    public VacunacionServiceImpl(VacunacionRepository vacunacionRepository,
                                 CiudadanoRepository ciudadanoRepository,
                                 PersonalSaludRepository personalSaludRepository,
                                 InventarioLoteRepository inventarioLoteRepository) {
        this.vacunacionRepository = vacunacionRepository;
        this.ciudadanoRepository = ciudadanoRepository;
        this.personalSaludRepository = personalSaludRepository;
        this.inventarioLoteRepository = inventarioLoteRepository;
    }

    @Override
    @Transactional
    public void registrarAplicacion(RegistrarVacunacionDTO dto, String emailPersonalSalud) {

        // 1. Buscar ciudadano
        Ciudadano ciudadano = ciudadanoRepository.findById(dto.getIdCiudadano())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        // 2. Buscar personal responsable (autenticado)
        PersonalSalud personal = personalSaludRepository.findPersonalActivoPorEmail(emailPersonalSalud)
                .orElseThrow(() -> new RecursoNoEncontradoException("Personal de salud no encontrado o inactivo"));

        // 3. Buscar lote de inventario
        InventarioLote inventario = inventarioLoteRepository.findById(dto.getIdInventario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de inventario no encontrado"));

        // 4. Validar lote activo
        if (!Boolean.TRUE.equals(inventario.getActivo())) {
            throw new IllegalStateException("El lote seleccionado está inactivo");
        }

        // 5. Validar stock disponible
        if (inventario.getStockActual() == null || inventario.getStockActual() <= 0) {
            throw new IllegalStateException("No hay stock disponible para aplicar esta vacuna");
        }

        // 6. Crear registro de vacunación
        Vacunacion vacunacion = Vacunacion.builder()
                .ciudadano(ciudadano)
                .personalSalud(personal)
                .inventario(inventario)
                .dosis(dto.getDosis())
                .fechaAplicacion(LocalDateTime.now())
                .observaciones(dto.getObservaciones())
                .reaccionesAdversas(dto.isReaccionesAdversas())
                .build();

        // 7. Descontar stock
        inventario.setStockActual(inventario.getStockActual() - 1);

        // 8. Si se agotó, inactivar lote
        if (inventario.getStockActual() == 0) {
            inventario.setActivo(false);
        }

        // 9. Guardar todo en la misma transacción
        inventarioLoteRepository.save(inventario);
        vacunacionRepository.save(vacunacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunacionResponseDTO> obtenerVacunasAplicadas(Long idCiudadano) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        return vacunacionRepository.findByCiudadano(ciudadano)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunacionResponseDTO> obtenerHistorial(Long idCiudadano) {
        Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        return vacunacionRepository.findByCiudadanoOrderByFechaAplicacionAsc(ciudadano)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private VacunacionResponseDTO mapToDTO(Vacunacion entity) {
        return VacunacionResponseDTO.builder()
                .idVacunacion(entity.getIdVacunacion())
                .vacuna(entity.getInventario().getVacuna().getNombre())
                .numeroLote(entity.getInventario().getNumeroLote())
                .dosis(entity.getDosis())
                .fechaAplicacion(entity.getFechaAplicacion())
                .aplicadoPor(entity.getPersonalSalud().getNombre() + " " + entity.getPersonalSalud().getApellido())
                .observaciones(entity.getObservaciones())
                .reaccionesAdversas(entity.isReaccionesAdversas())
                .build();
    }
}