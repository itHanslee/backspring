package com.vacunacion.vacunacion;

import com.vacunacion.common.enums.NumeroDosis;
import com.vacunacion.usuario.Ciudadano;
import com.vacunacion.usuario.PersonalSalud;
import com.vacunacion.vacuna.Vacuna;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vacunaciones")
@Getter
@Setter
@NoArgsConstructor
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaAplicacion;

    @Enumerated(EnumType.STRING)
    private NumeroDosis dosis;

    private String lote;
    private String observaciones;
    private boolean reaccionAdversa;

    @ManyToOne
    @JoinColumn(name = "personal_salud_id")
    private PersonalSalud personalSalud;

    @ManyToOne
    @JoinColumn(name = "vacuna_id")
    private Vacuna vacuna;

    @ManyToOne
    @JoinColumn(name = "ciudadano_id")
    private Ciudadano ciudadano;

    public void registrar() {
        // TODO: validar esquema + registrar + descontar inventario
    }

    public void descontarInventario() {
        // TODO: delegar en InventarioLote.descontarStock()
    }
}
