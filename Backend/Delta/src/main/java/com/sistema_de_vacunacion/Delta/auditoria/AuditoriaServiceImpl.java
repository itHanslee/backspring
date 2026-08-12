package com.sistema_de_vacunacion.Delta.auditoria;

import com.sistema_de_vacunacion.Delta.auditoria.dto.AuditoriaDTO;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;
import com.sistema_de_vacunacion.Delta.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(TipoAccionAuditoria tipoAccion,
                          String tablaAfectada,
                          Usuario usuario) {

        Auditoria auditoria = new Auditoria();
        auditoria.setTipoAccion(tipoAccion);
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setFechaAccion(LocalDateTime.now());
        auditoria.setUsuario(usuario);

        auditoriaRepository.save(auditoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarTodas() {
        return auditoriaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AuditoriaDTO mapToDTO(Auditoria entity) {
        AuditoriaDTO dto = new AuditoriaDTO();

        dto.setIdAuditoria(entity.getIdAuditoria());
        dto.setTipoAccion(entity.getTipoAccion());
        dto.setTablaAfectada(entity.getTablaAfectada());
        dto.setFechaAccion(entity.getFechaAccion());

        if (entity.getUsuario() != null) {
            dto.setIdUsuario(entity.getUsuario().getId());
        }

        return dto;
    }
}
