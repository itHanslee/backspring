package com.sistema_de_vacunacion.Delta.usuario;

import jakarta.persistence.Entity;

@Entity
public class Ciudadano extends Usuario {

    public void descargarCarne() {
        // TODO
    }

    public void verVacunasAplicadas() {
        // TODO
    }

    public void verVacunasPendientes() {
        // TODO
    }

    public void recibirRecordatorios() {
        // TODO
    }

    @Override
    public String getPermisos() {
        return "CIUDADANO";
    }
}
