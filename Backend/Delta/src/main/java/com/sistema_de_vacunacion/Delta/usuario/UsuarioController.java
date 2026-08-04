package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public UsuarioDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    public UsuarioDTO registrar(@RequestBody UsuarioDTO dto) {
        return usuarioService.registrar(dto);
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return usuarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
    }
}
