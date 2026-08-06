package com.sistema_de_vacunacion.Delta.vacuna;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema_de_vacunacion.Delta.vacuna.dto.EsquemaVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.InventarioLoteDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.VacunaDTO;
import com.sistema_de_vacunacion.Delta.vacuna.enums.EstadoVacuna;

@Service
public class VacunaServiceImpl implements VacunaService {

    private final VacunaRepository vacunaRepository;
    private final InventarioLoteRepository inventarioRepository;
    private final EsquemaVacunacionRepository esquemaRepository;

    public VacunaServiceImpl(VacunaRepository vacunaRepository,
                             InventarioLoteRepository inventarioRepository,
                             EsquemaVacunacionRepository esquemaRepository) {
        this.vacunaRepository = vacunaRepository;
        this.inventarioRepository = inventarioRepository;
        this.esquemaRepository = esquemaRepository;
    }

    @Override
    @Transactional
    public VacunaDTO crearVacuna(VacunaDTO dto) {
        if (vacunaRepository.existsByCodigo(dto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una vacuna con el código ingresado.");
        }
        Vacuna vacuna = mapToEntity(dto);
        vacuna.setEstado(EstadoVacuna.ACTIVA);
        return mapToDTO(vacunaRepository.save(vacuna));
    }

    @Override
    @Transactional(readOnly = true)
    public VacunaDTO obtenerVacunaPorId(Integer id) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con ID: " + id));
        return mapToDTO(vacuna);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunaDTO> listarTodasLasVacunas() {
        return vacunaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VacunaDTO actualizarVacuna(Integer id, VacunaDTO dto) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con ID:"+id));
        vacuna.setNombre(dto.getNombre());
        vacuna.setFabricante(dto.getFabricante());
        vacuna.setDosisTotales(dto.getDosisTotales());
        vacuna.setViaAdministracion(dto.getViaAdministracion());
        vacuna.setTemperaturaAlmacenamiento(dto.getTemperaturaAlmacenamiento());
        return mapToDTO(vacunaRepository.save(vacuna));
    }

    @Override
    @Transactional
    public void cambiarEstadoVacuna(Integer id, String estado) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con ID:"+id));
        vacuna.setEstado(EstadoVacuna.valueOf(estado));
        vacunaRepository.save(vacuna);
    }

    // --- MÉTODOS INVENTARIO LOTE ---

    @Override
    @Transactional
    public InventarioLoteDTO registrarLote(InventarioLoteDTO dto) {
        Vacuna vacuna = vacunaRepository.findById(dto.getIdVacuna())
                .orElseThrow(() -> new RuntimeException("Vacuna asociada no encontrada con ID: " + dto.getIdVacuna()));

        InventarioLote lote = InventarioLote.builder()
                .numeroLote(dto.getNumeroLote())
                .cantidadRecibida(dto.getCantidadRecibida())
                .stockActual(dto.getCantidadRecibida())
                .fechaVencimiento(dto.getFechaVencimiento())
                .activo(true)
                .vacuna(vacuna)
                .build();

        return mapLoteToDTO(inventarioRepository.save(lote));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioLoteDTO> listarLotesPorVacuna(Integer idVacuna) {
        return inventarioRepository.findByVacunaIdAndActivoTrue(idVacuna).stream()
                .map(this::mapLoteToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean descontarStockLote(Integer idLote, Integer cantidad) {
        InventarioLote lote = inventarioRepository.findById(idLote)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + idLote));

        if (lote.getStockActual() < cantidad) {
            return false;
        }

        lote.setStockActual(lote.getStockActual() - cantidad);
        if (lote.getStockActual() == 0) {
            lote.setActivo(false);
        }
        inventarioRepository.save(lote);
        return true;
    }

   
    @Override
    @Transactional
    public EsquemaVacunacionDTO registrarEsquema(EsquemaVacunacionDTO dto) {
         Vacuna vacuna = vacunaRepository.findById(dto.getIdVacuna())
             .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con ID: " + dto.getIdVacuna()));

         EsquemaVacunacion esquema = EsquemaVacunacion.builder()
                .numeroDosis(dto.getNumeroDosis())
                .edadMinimaAplicacion(dto.getEdadMinimaAplicacion())
                .intervaloDias(dto.getIntervaloDias())
                .criterioCalculo(dto.getCriterioCalculo())
                .vacuna(vacuna)
                .build();

    return mapEsquemaToDTO(esquemaRepository.save(esquema));
}


    @Override
    @Transactional(readOnly = true)
    public List<EsquemaVacunacionDTO> obtenerEsquemasPorVacuna(Integer idVacuna) {
        return esquemaRepository.findByVacunaIdOrderByNumeroDosisAsc(idVacuna).stream()
                .map(this::mapEsquemaToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarEsquema(Integer idEsquema) {
        if (!esquemaRepository.existsById(idEsquema)) {
            throw new RuntimeException("Esquema no encontrado con ID: " + idEsquema);
        }
        esquemaRepository.deleteById(idEsquema);
    }



    private VacunaDTO mapToDTO(Vacuna entity) {
        VacunaDTO dto = new VacunaDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setFabricante(entity.getFabricante());
        dto.setDosisTotales(entity.getDosisTotales());
        dto.setViaAdministracion(entity.getViaAdministracion());
        dto.setTemperaturaAlmacenamiento(entity.getTemperaturaAlmacenamiento());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    private Vacuna mapToEntity(VacunaDTO dto) {
        return Vacuna.builder()
                .id(dto.getId())
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .fabricante(dto.getFabricante())
                .dosisTotales(dto.getDosisTotales())
                .viaAdministracion(dto.getViaAdministracion())
                .temperaturaAlmacenamiento(dto.getTemperaturaAlmacenamiento())
                .estado(dto.getEstado())
                .build();
    }

    private InventarioLoteDTO mapLoteToDTO(InventarioLote entity) {
        InventarioLoteDTO dto = new InventarioLoteDTO();
        dto.setId(entity.getId());
        dto.setNumeroLote(entity.getNumeroLote());
        dto.setCantidadRecibida(entity.getCantidadRecibida());
        dto.setStockActual(entity.getStockActual());
        dto.setFechaVencimiento(entity.getFechaVencimiento());
        dto.setActivo(entity.getActivo());
        dto.setIdVacuna(entity.getVacuna().getId());
        return dto;
    }

    private EsquemaVacunacionDTO mapEsquemaToDTO(EsquemaVacunacion entity) {
    EsquemaVacunacionDTO dto = new EsquemaVacunacionDTO();
    dto.setId(entity.getId());
    dto.setEdadMinimaAplicacion(entity.getEdadMinimaAplicacion());
    dto.setNumeroDosis(entity.getNumeroDosis());
    dto.setIntervaloDias(entity.getIntervaloDias());
    dto.setCriterioCalculo(entity.getCriterioCalculo());
    dto.setIdVacuna(entity.getVacuna().getId());
    return dto;
}
}