package com.sistema_de_vacunacion.Delta.usuario;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import com.sistema_de_vacunacion.Delta.usuario.enums.EstadoUsuario;
import com.sistema_de_vacunacion.Delta.usuario.enums.Genero;
import com.sistema_de_vacunacion.Delta.usuario.enums.TipoDocumento;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario")
@Getter
@Setter
@NoArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int id;

    @Column(name = "numero_documento", unique = true, nullable = false)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    private String nombre;
    private String apellido;

    @Column(name = "correo", unique = true, nullable = false)
    private String email;

    @Column(name = "contrasena")
    private String contrasena;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    private String direccion;

    public void iniciarSesion() {
        // TODO: delegar en Spring Security
    }

    public void cerrarSesion() {
        // TODO: invalidar sesión/token
    }

    /**
     * Cada subclase define qué permisos/rol representa.
     * Punto clave de polimorfismo: se resuelve en tiempo de ejecución.
     */
    public abstract String getPermisos();
}
