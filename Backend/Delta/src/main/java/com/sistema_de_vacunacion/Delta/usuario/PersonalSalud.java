package com.sistema_de_vacunacion.Delta.usuario;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PersonalSalud extends Usuario {

    private String cargo;

    public void registrarAplicacionVacuna() {
        // TODO
    }

    public void registrarCiudadano() {
        // TODO
    }

    public void actualizarCiudadano() {
        // TODO
    }

    public void consultarHistorial() {
        // TODO
    }

    public void verStock() {
        // TODO
    }

    public void generarReporte() {
        // TODO
    }

    @Override
    public String getPermisos() {
        return "PERSONAL_SALUD";
    }
}
