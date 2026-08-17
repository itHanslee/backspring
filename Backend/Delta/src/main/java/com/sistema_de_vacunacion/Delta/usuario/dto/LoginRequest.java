package com.sistema_de_vacunacion.Delta.usuario.dto;

import com.sistema_de_vacunacion.Delta.usuario.enums.TipoDocumento;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    private TipoDocumento tipoDocumento;
}