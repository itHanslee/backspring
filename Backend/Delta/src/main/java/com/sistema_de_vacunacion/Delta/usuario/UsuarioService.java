package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    UsuarioDTO registrar(UsuarioDTO dto);
    UsuarioDTO actualizar(Integer id, UsuarioDTO dto);
    UsuarioDTO buscarPorId(Integer id);
    List<UsuarioDTO> listarTodos();
    void desactivar(Integer id);
}
