package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioDTO registrar(UsuarioDTO dto) {
        // TODO: mapear DTO -> entidad concreta según rol y guardar
        return null;
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        // TODO
        return null;
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) {
        // TODO
        return null;
    }

    @Override
    public List<UsuarioDTO> listarTodos() {
        // TODO
        return null;
    }

    @Override
    public void desactivar(Long id) {
        // TODO
    }
}
