package com.sistema_de_vacunacion.Delta.usuario.dto;

import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;
import com.sistema_de_vacunacion.Delta.usuario.enums.Genero;
import com.sistema_de_vacunacion.Delta.usuario.enums.TipoDocumento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String numeroDocumento;
    private TipoDocumento tipoDocumento;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private EstadoUsuario estado;
    private LocalDate fechaNacimiento;
    private Genero genero;
    private String direccion;
    private String rol; // viene de getPermisos()
}
