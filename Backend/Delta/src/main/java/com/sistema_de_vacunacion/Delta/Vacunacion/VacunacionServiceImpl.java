package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSalud;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSaludRepository;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacion;
import com.sistema_de_vacunacion.Delta.vacuna.EsquemaVacunacionRepository;
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLote;
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLoteRepository;
import com.sistema_de_vacunacion.Delta.vacuna.Vacuna;
import com.sistema_de_vacunacion.Delta.vacuna.VacunaRepository;
import com.sistema_de_vacunacion.Delta.vacuna.enums.EstadoVacuna;
import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;
import com.sistema_de_vacunacion.Delta.vacuna.strategy.ServicioCalculadorEsquema;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.RegistrarVacunacionDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunaPendienteDTO;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.VacunacionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.sistema_de_vacunacion.Delta.recordatorio.RecordatorioService;
import com.sistema_de_vacunacion.Delta.recordatorio.dto.RecordatorioDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacunacionServiceImpl implements VacunacionService {

        private final VacunacionRepository vacunacionRepository;
        private final CiudadanoRepository ciudadanoRepository;
        private final PersonalSaludRepository personalSaludRepository;
        private final InventarioLoteRepository inventarioLoteRepository;
        private final EsquemaVacunacionRepository esquemaRepository;
        private final VacunaRepository vacunaRepository;
        private final ServicioCalculadorEsquema calculadorEsquema;
        private final RecordatorioService recordatorioService;

        public VacunacionServiceImpl(VacunacionRepository vacunacionRepository,
                        CiudadanoRepository ciudadanoRepository,
                        PersonalSaludRepository personalSaludRepository,
                        InventarioLoteRepository inventarioLoteRepository,
                        EsquemaVacunacionRepository esquemaRepository,
                        VacunaRepository vacunaRepository,
                        ServicioCalculadorEsquema calculadorEsquema,
                        RecordatorioService recordatorioService) {
                this.vacunacionRepository = vacunacionRepository;
                this.ciudadanoRepository = ciudadanoRepository;
                this.personalSaludRepository = personalSaludRepository;
                this.inventarioLoteRepository = inventarioLoteRepository;
                this.esquemaRepository = esquemaRepository;
                this.vacunaRepository = vacunaRepository;
                this.calculadorEsquema = calculadorEsquema;
                this.recordatorioService = recordatorioService;
        }

        @Override
        @Transactional
        public void registrarAplicacion(RegistrarVacunacionDTO dto, String emailPersonalSalud) {

                // 1. Buscar ciudadano
                Ciudadano ciudadano = ciudadanoRepository.findById(dto.getIdCiudadano())
                                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

                // 2. Buscar personal responsable (autenticado)
                PersonalSalud personal = personalSaludRepository.findPersonalActivoPorEmail(emailPersonalSalud)
                                .orElseThrow(() -> new RecursoNoEncontradoException(
                                                "Personal de salud no encontrado o inactivo"));

                // 3. Buscar lote de inventario
                InventarioLote inventario = inventarioLoteRepository.findById(dto.getIdInventario())
                                .orElseThrow(() -> new RecursoNoEncontradoException(
                                                "Lote de inventario no encontrado"));

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

                System.out.println("=================================");
                System.out.println("VACUNACION GUARDADA");
                System.out.println("CIUDADANO: " + ciudadano.getId());
                System.out.println("DOSIS: " + vacunacion.getDosis());

                List<RecordatorioDTO> recordatorios = recordatorioService.generarRecordatorios(ciudadano.getId());

                System.out.println("RECORDATORIOS GENERADOS: " + recordatorios.size());

                System.out.println("=================================");
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
                                .aplicadoPor(entity.getPersonalSalud().getNombre() + " "
                                                + entity.getPersonalSalud().getApellido())
                                .observaciones(entity.getObservaciones())
                                .reaccionesAdversas(entity.isReaccionesAdversas())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<VacunaPendienteDTO> obtenerVacunasPendientes(
                        Long idCiudadano) {

                Ciudadano ciudadano = ciudadanoRepository.findById(idCiudadano)
                                .orElseThrow(() -> new RecursoNoEncontradoException(
                                                "Ciudadano no encontrado"));

                List<Vacunacion> vacunaciones = vacunacionRepository.findByCiudadanoOrderByFechaAplicacionAsc(
                                ciudadano);

                List<VacunaPendienteDTO> pendientes = new ArrayList<>();

                for (Vacuna vacuna : vacunaRepository.findAll()) {

                        if (vacuna.getEstado() != EstadoVacuna.ACTIVA) {
                                continue;
                        }

                        List<EsquemaVacunacion> esquemas = esquemaRepository.findByVacunaIdOrderByDosisNumeroAsc(
                                        vacuna.getId());

                        if (esquemas.isEmpty()) {
                                continue;
                        }

                        List<Vacunacion> vacunacionesVacuna = vacunaciones.stream()
                                        .filter(v -> v.getInventario() != null
                                                        && v.getInventario().getVacuna() != null
                                                        && v.getInventario()
                                                                        .getVacuna()
                                                                        .getId()
                                                                        .equals(vacuna.getId()))
                                        .toList();

                        EsquemaVacunacion siguiente = buscarSiguienteDosisInicial(
                                        vacunacionesVacuna,
                                        esquemas);

                        if (siguiente != null) {

                                LocalDate fechaUltimaDosis = vacunacionesVacuna.stream()
                                                .map(Vacunacion::getFechaAplicacion)
                                                .filter(f -> f != null)
                                                .max(LocalDateTime::compareTo)
                                                .map(LocalDateTime::toLocalDate)
                                                .orElse(null);

                                LocalDate fechaProgramada = calculadorEsquema.calcularProximaFecha(
                                                siguiente,
                                                ciudadano.getFechaNacimiento(),
                                                fechaUltimaDosis);

                                agregarPendiente(
                                                pendientes,
                                                ciudadano,
                                                vacuna,
                                                siguiente,
                                                fechaProgramada);

                                continue;
                        }

                        EsquemaVacunacion refuerzo = esquemas.stream()
                                        .filter(e -> e.getDosisNumero() == NumeroDosis.Refuerzo)
                                        .findFirst()
                                        .orElse(null);

                        if (refuerzo == null) {
                                continue;
                        }

                        LocalDateTime ultimaDosisInicial = buscarFechaUltimaDosisInicial(
                                        vacunacionesVacuna,
                                        esquemas);

                        if (ultimaDosisInicial == null) {
                                continue;
                        }

                        LocalDate fechaRefuerzo = calculadorEsquema.calcularProximaFecha(
                                        refuerzo,
                                        ciudadano.getFechaNacimiento(),
                                        ultimaDosisInicial.toLocalDate());

                        agregarPendiente(
                                        pendientes,
                                        ciudadano,
                                        vacuna,
                                        refuerzo,
                                        fechaRefuerzo);
                }

                return pendientes;
        }

        private void agregarPendiente(
                        List<VacunaPendienteDTO> pendientes,
                        Ciudadano ciudadano,
                        Vacuna vacuna,
                        EsquemaVacunacion esquema,
                        LocalDate fechaProgramada) {

                InventarioLote lote = inventarioLoteRepository
                                .findFirstByVacunaIdAndActivoTrueAndStockActualGreaterThanOrderByFechaVencimientoAsc(
                                                vacuna.getId(),
                                                0)
                                .orElse(null);

                VacunaPendienteDTO dto = new VacunaPendienteDTO();

                dto.setIdCiudadano(ciudadano.getId());
                dto.setNombreCiudadano(
                                ciudadano.getNombre() + " " + ciudadano.getApellido());
                dto.setDocumentoCiudadano(
                                ciudadano.getNumeroDocumento());

                dto.setVacunaNombre(vacuna.getNombre());
                dto.setDosis(esquema.getDosisNumero());
                dto.setFechaProgramada(fechaProgramada);

                dto.setDescripcion(
                                "Próxima dosis " +
                                                esquema.getDosisNumero() +
                                                " de " +
                                                vacuna.getNombre());

                if (lote != null) {
                        dto.setIdInventario(lote.getId());
                        dto.setNumeroLote(lote.getNumeroLote());
                }

                pendientes.add(dto);
        }

        private EsquemaVacunacion buscarSiguienteDosisInicial(
                        List<Vacunacion> vacunaciones,
                        List<EsquemaVacunacion> esquemas) {

                NumeroDosis[] orden = {
                                NumeroDosis.Unica,
                                NumeroDosis.Primera,
                                NumeroDosis.Segunda,
                                NumeroDosis.Tercera
                };

                for (NumeroDosis dosis : orden) {

                        EsquemaVacunacion esquema = esquemas.stream()
                                        .filter(e -> e.getDosisNumero() == dosis)
                                        .findFirst()
                                        .orElse(null);

                        if (esquema == null) {
                                continue;
                        }

                        boolean aplicada = vacunaciones.stream()
                                        .anyMatch(v -> v.getDosis() == dosis);

                        if (!aplicada) {
                                return esquema;
                        }
                }

                return null;
        }

        private LocalDateTime buscarFechaUltimaDosisInicial(
                        List<Vacunacion> vacunaciones,
                        List<EsquemaVacunacion> esquemas) {

                NumeroDosis[] orden = {
                                NumeroDosis.Tercera,
                                NumeroDosis.Segunda,
                                NumeroDosis.Primera,
                                NumeroDosis.Unica
                };

                for (NumeroDosis dosis : orden) {

                        boolean existeEnEsquema = esquemas.stream()
                                        .anyMatch(e -> e.getDosisNumero() == dosis);

                        if (!existeEnEsquema) {
                                continue;
                        }

                        LocalDateTime fecha = vacunaciones.stream()
                                        .filter(v -> v.getDosis() == dosis)
                                        .map(Vacunacion::getFechaAplicacion)
                                        .filter(f -> f != null)
                                        .max(LocalDateTime::compareTo)
                                        .orElse(null);

                        if (fecha != null) {
                                return fecha;
                        }
                }

                return null;
        }

        @Override
        @Transactional(readOnly = true)
        public void verificarAccesoCiudadano(
                        Long idCiudadano,
                        Authentication authentication) {

                // El personal de salud puede consultar cualquier ciudadano
                boolean esPersonalSalud = authentication.getAuthorities()
                                .stream()
                                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PERSONAL_SALUD"));

                if (esPersonalSalud) {
                        return;
                }

                // Obtener el correo del usuario autenticado
                String emailAutenticado = authentication.getName();

                // Buscar al ciudadano correspondiente al usuario autenticado
                Ciudadano ciudadanoAutenticado = ciudadanoRepository
                                .findByEmail(emailAutenticado)
                                .orElseThrow(() -> new AccessDeniedException(
                                                "No se encontró el ciudadano autenticado"));

                // Verificar que solo pueda consultar sus propios datos
                if (!ciudadanoAutenticado.getId().equals(idCiudadano)) {
                        throw new AccessDeniedException(
                                        "No tienes permiso para consultar las vacunas de otro ciudadano");
                }
        }
}