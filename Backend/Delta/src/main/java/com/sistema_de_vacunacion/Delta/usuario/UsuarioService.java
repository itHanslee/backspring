package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.AuthResponse;
import com.sistema_de_vacunacion.Delta.usuario.dto.LoginRequest;
import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioService { 
    AuthResponse login(LoginRequest request);
    UsuarioDTO registrar(UsuarioDTO dto);
    UsuarioDTO actualizar(Long id, UsuarioDTO dto);
    UsuarioDTO buscarPorId(Long id);
    List<UsuarioDTO> listarTodos();
    void desactivar(Long id);
}
