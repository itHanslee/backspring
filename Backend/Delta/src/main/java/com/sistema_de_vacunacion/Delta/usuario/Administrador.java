package com.sistema_de_vacunacion.Delta.usuario;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {

    public void gestionarPersonalSalud() {
        // TODO
    }

    public void verAuditoriaGlobal() {
        // TODO
    }

    public void gestionarVacunas() {
        // TODO
    }

    @Override
    public String getPermisos() {
        return "ADMIN";
    }
}
