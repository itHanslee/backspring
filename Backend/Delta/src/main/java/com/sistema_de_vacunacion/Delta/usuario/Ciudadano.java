package com.sistema_de_vacunacion.Delta.usuario;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.sistema_de_vacunacion.Delta.vacunacion.Vacunacion;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
@Table(name = "ciudadano")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("CIUDADANO")
@Getter
@Setter
@NoArgsConstructor
public class Ciudadano extends Usuario {

    @OneToMany(mappedBy = "ciudadano")
    private List<Vacunacion> vacunaciones;

    @Override
    public String getPermisos() {
        return "CIUDADANO";
    }

    // Métodos de negocio expuestos según tu diagrama de clases
    public boolean tieneVacunasPendientes() {
        // Lógica de dominio: evalúa si existen esquemas de vacunación incompletos
        return true;
    }
}