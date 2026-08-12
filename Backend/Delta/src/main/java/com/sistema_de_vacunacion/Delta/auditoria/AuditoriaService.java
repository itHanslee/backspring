package com.sistema_de_vacunacion.Delta.auditoria;

import com.sistema_de_vacunacion.Delta.auditoria.dto.AuditoriaDTO;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;
import com.sistema_de_vacunacion.Delta.usuario.Usuario;

import java.util.List;

public interface AuditoriaService {

    void registrar(TipoAccionAuditoria tipoAccion,
                   String tablaAfectada,
                   Usuario usuario,
                   String datosAnteriores,
                   String datosNuevos);

    List<AuditoriaDTO> listarTodas();
}