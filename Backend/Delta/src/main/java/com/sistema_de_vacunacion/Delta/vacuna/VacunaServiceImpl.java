package com.sistema_de_vacunacion.Delta.vacuna;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema_de_vacunacion.Delta.auditoria.AuditoriaService;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;
import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.Usuario;
import com.sistema_de_vacunacion.Delta.usuario.UsuarioRepository;
import com.sistema_de_vacunacion.Delta.vacuna.dto.EsquemaVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.InventarioLoteDTO;
import com.sistema_de_vacunacion.Delta.vacuna.dto.VacunaDTO;
import com.sistema_de_vacunacion.Delta.vacuna.enums.EstadoVacuna;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class VacunaServiceImpl implements VacunaService {

    private final VacunaRepository vacunaRepository;
    private final InventarioLoteRepository inventarioRepository;
    private final EsquemaVacunacionRepository esquemaRepository;

    // Auditoría
    private final AuditoriaService auditoriaService;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public VacunaServiceImpl(
            VacunaRepository vacunaRepository,
            InventarioLoteRepository inventarioRepository,
            EsquemaVacunacionRepository esquemaRepository,
            AuditoriaService auditoriaService,
            UsuarioRepository usuarioRepository,
            ObjectMapper objectMapper) {

        this.vacunaRepository = vacunaRepository;
        this.inventarioRepository = inventarioRepository;
        this.esquemaRepository = esquemaRepository;

        this.auditoriaService = auditoriaService;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // VACUNAS
    // =========================================================

    @Override
    @Transactional
    public VacunaDTO crearVacuna(VacunaDTO dto) {

        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la vacuna es obligatorio."
            );
        }

        if (vacunaRepository.existsByCodigo(dto.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe una vacuna con el código ingresado."
            );
        }

        Vacuna vacuna = mapToEntity(dto);

        /*
         * La vacuna se crea inicialmente INACTIVA.
         *
         * Después de registrar el lote se ejecuta
         * actualizarEstadoVacuna().
         */
        Vacuna guardada =
                vacunaRepository.save(vacuna);

        // Registrar creación en auditoría
        registrarAuditoria(
                TipoAccionAuditoria.CREAR,
                "vacuna",
                null,
                mapToDTO(guardada)
        );

        return mapToDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public VacunaDTO obtenerVacunaPorId(Integer id) {

        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vacuna no encontrada con ID: " + id
                        )
                );

        return mapToDTO(vacuna);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunaDTO> listarTodasLasVacunas() {

        /*
         * Antes de devolver la información comprobamos
         * los estados por si existe algún lote que
         * haya vencido.
         */
        actualizarEstadosPorVencimiento();

        return vacunaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VacunaDTO actualizarVacuna(
            Integer id,
            VacunaDTO dto) {

        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vacuna no encontrada con ID: " + id
                        )
                );

        /*
         * Guardamos una copia de los datos anteriores
         * antes de modificar la vacuna.
         */
        VacunaDTO datosAnteriores =
                mapToDTO(vacuna);

        vacuna.setNombre(dto.getNombre());
        vacuna.setFabricante(dto.getFabricante());
        vacuna.setDosisTotales(dto.getDosisTotales());
        vacuna.setViaAdministracion(
                dto.getViaAdministracion()
        );
        vacuna.setTemperaturaAlmacenamiento(
                dto.getTemperaturaAlmacenamiento()
        );

        Vacuna guardada =
                vacunaRepository.save(vacuna);

        /*
         * Después de modificar los datos de la vacuna
         * comprobamos nuevamente su estado.
         */
        actualizarEstadoVacuna(guardada);

        /*
         * Registrar edición en auditoría.
         */
        registrarAuditoria(
                TipoAccionAuditoria.EDITAR,
                "vacuna",
                datosAnteriores,
                mapToDTO(guardada)
        );

        return mapToDTO(guardada);
    }

    @Override
    @Transactional
    public void cambiarEstadoVacuna(
            Integer id,
            String estado) {

        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vacuna no encontrada con ID: " + id
                        )
                );

        EstadoVacuna estadoAnterior =
                vacuna.getEstado();

        vacuna.setEstado(
                EstadoVacuna.valueOf(
                        estado.toUpperCase()
                )
        );

        vacunaRepository.save(vacuna);

        /*
         * Registrar cambio manual de estado.
         */
        registrarAuditoria(
                TipoAccionAuditoria.EDITAR,
                "vacuna",
                "Estado anterior: " + estadoAnterior,
                "Estado nuevo: " + vacuna.getEstado()
        );
    }

    // =========================================================
    // LOTES
    // =========================================================

    @Override
    @Transactional
    public InventarioLoteDTO registrarLote(
            InventarioLoteDTO dto) {

        if (dto.getIdVacuna() == null) {

            throw new IllegalArgumentException(
                    "Debe indicar la vacuna del lote."
            );
        }

        if (dto.getNumeroLote() == null ||
                dto.getNumeroLote().isBlank()) {

            throw new IllegalArgumentException(
                    "El número de lote es obligatorio."
            );
        }

        if (dto.getCantidadRecibida() == null ||
                dto.getCantidadRecibida() <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad recibida debe ser mayor que cero."
            );
        }

        if (dto.getFechaVencimiento() == null) {

            throw new IllegalArgumentException(
                    "La fecha de vencimiento es obligatoria."
            );
        }

        Vacuna vacuna = vacunaRepository.findById(
                dto.getIdVacuna()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Vacuna asociada no encontrada con ID: "
                                + dto.getIdVacuna()
                )
        );

        /*
         * Evitamos registrar dos lotes con el mismo número.
         */
        if (!inventarioRepository
                .findByNumeroLote(dto.getNumeroLote())
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Ya existe un lote con el número: "
                            + dto.getNumeroLote()
            );
        }

        LocalDate hoy = LocalDate.now();

        boolean vencido =
                dto.getFechaVencimiento()
                        .isBefore(hoy);

        boolean activo =
                !vencido &&
                dto.getCantidadRecibida() > 0;

        InventarioLote lote =
                InventarioLote.builder()
                        .numeroLote(
                                dto.getNumeroLote()
                        )
                        .cantidadRecibida(
                                dto.getCantidadRecibida()
                        )
                        .stockActual(
                                dto.getCantidadRecibida()
                        )
                        .fechaVencimiento(
                                dto.getFechaVencimiento()
                        )
                        .activo(activo)
                        .vacuna(vacuna)
                        .build();

        InventarioLote guardado =
                inventarioRepository.save(lote);

        /*
         * Determina si la vacuna debe quedar ACTIVA
         * o INACTIVA.
         */
        actualizarEstadoVacuna(vacuna);

        /*
         * Registrar creación del lote en auditoría.
         */
        registrarAuditoria(
                TipoAccionAuditoria.CREAR,
                "inventario_lote",
                null,
                mapLoteToDTO(guardado)
        );

        return mapLoteToDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioLoteDTO> listarLotesPorVacuna(
            Integer idVacuna) {

        return inventarioRepository
                .findByVacunaIdAndActivoTrue(idVacuna)
                .stream()
                .map(this::mapLoteToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioLoteDTO> listarTodosLosLotesPorVacuna(
            Integer idVacuna) {

        return inventarioRepository
                .findByVacunaId(idVacuna)
                .stream()
                .map(this::mapLoteToDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // DESCONTAR STOCK
    // =========================================================

    @Override
    @Transactional
    public boolean descontarStockLote(
            Integer idLote,
            Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            return false;
        }

        InventarioLote lote =
                inventarioRepository.findById(idLote)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lote no encontrado con ID: "
                                                + idLote
                                )
                        );

        LocalDate hoy = LocalDate.now();

        /*
         * Si el lote está vencido, no se puede utilizar.
         */
        if (lote.getFechaVencimiento() != null &&
                lote.getFechaVencimiento()
                        .isBefore(hoy)) {

            lote.setActivo(false);

            inventarioRepository.save(lote);

            actualizarEstadoVacuna(
                    lote.getVacuna()
            );

            return false;
        }

        /*
         * Si ya está inactivo tampoco puede utilizarse.
         */
        if (!Boolean.TRUE.equals(lote.getActivo())) {
            return false;
        }

        /*
         * Verificamos que exista suficiente stock.
         */
        if (lote.getStockActual() == null ||
                lote.getStockActual() < cantidad) {

            return false;
        }

        int nuevoStock =
                lote.getStockActual() - cantidad;

        lote.setStockActual(
                Math.max(nuevoStock, 0)
        );

        /*
         * Si el stock llega a cero,
         * el lote queda inactivo.
         */
        if (lote.getStockActual() <= 0) {

            lote.setStockActual(0);
            lote.setActivo(false);
        }

        inventarioRepository.save(lote);

        /*
         * Recalculamos el estado de la vacuna.
         */
        actualizarEstadoVacuna(
                lote.getVacuna()
        );

        return true;
    }

    // =========================================================
    // ACTUALIZACIÓN DE ESTADO DE VACUNA
    // =========================================================

    private void actualizarEstadoVacuna(
            Vacuna vacuna) {

        if (vacuna == null ||
                vacuna.getId() == null) {
            return;
        }

        LocalDate hoy = LocalDate.now();

        List<InventarioLote> lotes =
                inventarioRepository
                        .findByVacunaId(vacuna.getId());

        boolean existeLoteValido = false;

        for (InventarioLote lote : lotes) {

            boolean stockDisponible =
                    lote.getStockActual() != null &&
                    lote.getStockActual() > 0;

            boolean noVencido =
                    lote.getFechaVencimiento() != null &&
                    !lote.getFechaVencimiento()
                            .isBefore(hoy);

            boolean activo =
                    Boolean.TRUE.equals(
                            lote.getActivo()
                    );

            /*
             * Si el lote está vencido o sin stock,
             * lo dejamos inactivo.
             */
            if (!stockDisponible ||
                    !noVencido) {

                if (Boolean.TRUE.equals(
                        lote.getActivo())) {

                    lote.setActivo(false);

                    inventarioRepository.save(lote);
                }

                continue;
            }

            /*
             * Existe al menos un lote disponible.
             */
            if (activo) {
                existeLoteValido = true;
            }
        }

        if (existeLoteValido) {

            vacuna.setEstado(
                    EstadoVacuna.ACTIVA
            );

        } else {

            vacuna.setEstado(
                    EstadoVacuna.INACTIVA
            );
        }

        vacunaRepository.save(vacuna);
    }

    // =========================================================
    // VENCIMIENTO AUTOMÁTICO
    // =========================================================

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void actualizarLotesVencidos() {

        actualizarEstadosPorVencimiento();
    }

    @Transactional
    protected void actualizarEstadosPorVencimiento() {

        LocalDate hoy = LocalDate.now();

        List<InventarioLote> lotes =
                inventarioRepository.findAll();

        for (InventarioLote lote : lotes) {

            if (lote.getFechaVencimiento() != null &&
                    lote.getFechaVencimiento()
                            .isBefore(hoy)) {

                lote.setActivo(false);

                inventarioRepository.save(lote);
            }
        }

        /*
         * Después de marcar los lotes vencidos,
         * recalculamos el estado de cada vacuna.
         */
        List<Vacuna> vacunas =
                vacunaRepository.findAll();

        for (Vacuna vacuna : vacunas) {

            actualizarEstadoVacuna(vacuna);
        }
    }

    // =========================================================
    // ESQUEMAS DE VACUNACIÓN
    // =========================================================

    @Override
    @Transactional
    public EsquemaVacunacionDTO registrarEsquema(
            EsquemaVacunacionDTO dto) {

        Vacuna vacuna =
                vacunaRepository.findById(dto.getIdVacuna())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vacuna no encontrada con ID: "
                                                + dto.getIdVacuna()
                                )
                        );

        EsquemaVacunacion esquema =
                EsquemaVacunacion.builder()
                        .dosisNumero(
                                dto.getDosisNumero()
                        )
                        .unidadTiempoEdad(
                                dto.getUnidadTiempoEdad()
                        )
                        .intervaloDias(
                                dto.getIntervaloDias()
                        )
                        .criterioCalculo(
                                dto.getCriterioCalculo()
                        )
                        .observaciones(
                                dto.getObservaciones()
                        )
                        .vacuna(vacuna)
                        .build();

        EsquemaVacunacion guardado =
                esquemaRepository.save(esquema);

        /*
         * Registrar creación del esquema.
         */
        registrarAuditoria(
                TipoAccionAuditoria.CREAR,
                "esquema_vacunacion",
                null,
                mapEsquemaToDTO(guardado)
        );

        return mapEsquemaToDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EsquemaVacunacionDTO>
    obtenerEsquemasPorVacuna(
            Integer idVacuna) {

        return esquemaRepository
                .findByVacunaIdOrderByDosisNumeroAsc(
                        idVacuna
                )
                .stream()
                .map(this::mapEsquemaToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarEsquema(
            Integer idEsquema) {

        if (!esquemaRepository.existsById(idEsquema)) {

            throw new RuntimeException(
                    "Esquema no encontrado con ID: "
                            + idEsquema
            );
        }

        /*
         * Antes de eliminarlo buscamos la información
         * para poder guardarla en auditoría.
         */
        EsquemaVacunacion esquema =
                esquemaRepository.findById(idEsquema)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Esquema no encontrado con ID: "
                                                + idEsquema
                                )
                        );

        EsquemaVacunacionDTO datosAnteriores =
                mapEsquemaToDTO(esquema);

        esquemaRepository.deleteById(idEsquema);

        /*
         * Registrar eliminación.
         */
        registrarAuditoria(
                TipoAccionAuditoria.ELIMINAR,
                "esquema_vacunacion",
                datosAnteriores,
                null
        );
    }

    // =========================================================
    // MAPPERS
    // =========================================================

    private VacunaDTO mapToDTO(
            Vacuna entity) {

        VacunaDTO dto = new VacunaDTO();

        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setFabricante(entity.getFabricante());
        dto.setDosisTotales(
                entity.getDosisTotales()
        );
        dto.setViaAdministracion(
                entity.getViaAdministracion()
        );
        dto.setTemperaturaAlmacenamiento(
                entity.getTemperaturaAlmacenamiento()
        );
        dto.setEstado(
                entity.getEstado()
        );

        return dto;
    }

    private Vacuna mapToEntity(
            VacunaDTO dto) {

        return Vacuna.builder()
                .id(dto.getId())
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .fabricante(dto.getFabricante())
                .dosisTotales(dto.getDosisTotales() )
                .viaAdministracion(dto.getViaAdministracion())
                .temperaturaAlmacenamiento(dto.getTemperaturaAlmacenamiento())
                .estado(EstadoVacuna.INACTIVA)
                .build();
    }

    private InventarioLoteDTO mapLoteToDTO(
            InventarioLote entity) {

        InventarioLoteDTO dto =
                new InventarioLoteDTO();

        dto.setId(entity.getId());
        dto.setNumeroLote(
                entity.getNumeroLote()
        );
        dto.setCantidadRecibida(
                entity.getCantidadRecibida()
        );
        dto.setStockActual(
                entity.getStockActual()
        );
        dto.setFechaVencimiento(
                entity.getFechaVencimiento()
        );
        dto.setActivo(
                Boolean.TRUE.equals(
                        entity.getActivo()
                )
        );
        dto.setIdVacuna(
                entity.getVacuna().getId()
        );

        return dto;
    }

    private EsquemaVacunacionDTO mapEsquemaToDTO(
            EsquemaVacunacion entity) {

        EsquemaVacunacionDTO dto =
                new EsquemaVacunacionDTO();

        dto.setId(entity.getId());
        dto.setDosisNumero(
                entity.getDosisNumero()
        );
        dto.setEdadMinimaAplicacion(
                entity.getEdadMinimaAplicacion()
        );
        dto.setEdadMaximaAplicacion(
                entity.getEdadMaximaAplicacion()
        );
        dto.setUnidadTiempoEdad(
                entity.getUnidadTiempoEdad()
        );
        dto.setIntervaloDias(
                entity.getIntervaloDias()
        );
        dto.setCriterioCalculo(
                entity.getCriterioCalculo()
        );
        dto.setObservaciones(
                entity.getObservaciones()
        );
        dto.setIdVacuna(
                entity.getVacuna().getId()
        );

        return dto;
    }

    // =========================================================
    // AUDITORÍA
    // =========================================================

    /**
     * Obtiene el usuario que está autenticado actualmente.
     */
    private Usuario usuarioAutenticado() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario autenticado no encontrado"
                        )
                );
    }

    /**
     * Registra una acción en la tabla de auditoría.
     *
     * Jackson 3 utiliza:
     *
     * tools.jackson.core.JacksonException
     * tools.jackson.databind.ObjectMapper
     */
    private void registrarAuditoria(
            TipoAccionAuditoria tipo,
            String tabla,
            Object anterior,
            Object nuevo) {

        try {

            String datosAnteriores =
                    anterior == null
                            ? null
                            : objectMapper.writeValueAsString(
                                    anterior
                            );

            String datosNuevos =
                    nuevo == null
                            ? null
                            : objectMapper.writeValueAsString(
                                    nuevo
                            );

            auditoriaService.registrar(
                    tipo,
                    tabla,
                    usuarioAutenticado(),
                    datosAnteriores,
                    datosNuevos
            );

        } catch (JacksonException e) {

            throw new RuntimeException(
                    "Error al registrar auditoría",
                    e
            );
        }
    }
}