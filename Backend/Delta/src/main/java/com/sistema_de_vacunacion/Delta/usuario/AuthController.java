package com.sistema_de_vacunacion.Delta.usuario;

import com.sistema_de_vacunacion.Delta.usuario.dto.AuthResponse;
import com.sistema_de_vacunacion.Delta.usuario.dto.LoginRequest;
import com.sistema_de_vacunacion.Delta.usuario.jwt.JwtUtils; // Clase encargada de firmar/generar tokens JWT
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Valida la clave con Argon2 mediante Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.iniciarSesion(); // Hook de tu método de dominio

        String jwt = jwtUtils.generarToken(usuario.getEmail(), usuario.getPermisos());

        return ResponseEntity.ok(new AuthResponse(jwt, "Bearer", usuario.getEmail(), usuario.getPermisos()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Para JWT stateless el cliente borra el token en el frontend;
        // opcionalmente puedes invalidarlo o meterlo en lista negra.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Sesión cerrada exitosamente.");
    }
}