package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.AuthResponse;
import com.sistema_de_vacunacion.Delta.usuario.dto.LoginRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

                 System.out.println("🔥🔥🔥 ENTRO AL LOGIN EN RENDER 🔥🔥🔥");
    

        return ResponseEntity.ok(
                usuarioService.login(request));
    }
}
