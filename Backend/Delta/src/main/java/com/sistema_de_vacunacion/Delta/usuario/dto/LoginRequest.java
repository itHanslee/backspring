package com.sistema_de_vacunacion.Delta.usuario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String contrasena;
}