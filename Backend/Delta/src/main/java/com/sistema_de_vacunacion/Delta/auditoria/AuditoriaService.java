package com.sistema_de_vacunacion.Delta.auditoria;

import java.util.List;

import com.sistema_de_vacunacion.Delta.auditoria.dto.AuditoriaDTO;
import com.sistema_de_vacunacion.Delta.auditoria.enums.TipoAccionAuditoria;
import com.sistema_de_vacunacion.Delta.usuario.Usuario;

public interface AuditoriaService {

    void registrar(TipoAccionAuditoria tipoAccion,
                   String tabla,
                   Usuario usuario,
                   String datosAnteriores,
                   String datosNuevos);

    List<AuditoriaDTO> listarTodas();
}