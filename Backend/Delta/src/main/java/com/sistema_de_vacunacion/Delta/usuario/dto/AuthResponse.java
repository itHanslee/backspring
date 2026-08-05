package com.sistema_de_vacunacion.Delta.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipoToken;
    private String email;
    private String rol;
}