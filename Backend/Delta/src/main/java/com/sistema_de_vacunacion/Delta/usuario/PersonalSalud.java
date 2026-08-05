package com.sistema_de_vacunacion.Delta.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personal_salud")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
public class PersonalSalud extends Usuario {

    @Column(name = "cargo", nullable = false)
    private String cargo;

    @Override
    public String getPermisos() {
        return "PERSONAL_SALUD";
    }
}