package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSalud;
import com.sistema_de_vacunacion.Delta.vacuna.InventarioLote;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vacunacion")
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacunacion")
    private Integer idVacunacion;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDateTime fechaAplicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "dosis", nullable = false)
    private NumeroDosis dosis;

    @Column(length = 255)
    private String observaciones;

    @Column(name = "reacciones_adversas", nullable = false)
    private boolean reaccionesAdversas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudadano", nullable = false)
    private Ciudadano ciudadano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal_salud", nullable = false)
    private PersonalSalud personalSalud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventario", nullable = false)
    private InventarioLote inventario;

    public Vacunacion() {
    }

    public static VacunacionBuilder builder() {
        return new VacunacionBuilder();
    }

    public Integer getIdVacunacion() {
        return idVacunacion;
    }

    public void setIdVacunacion(Integer idVacunacion) {
        this.idVacunacion = idVacunacion;
    }

    public LocalDateTime getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDateTime fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public NumeroDosis getDosis() {
        return dosis;
    }

    public void setDosis(NumeroDosis dosis) {
        this.dosis = dosis;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isReaccionesAdversas() {
        return reaccionesAdversas;
    }

    public void setReaccionesAdversas(boolean reaccionesAdversas) {
        this.reaccionesAdversas = reaccionesAdversas;
    }

    public Ciudadano getCiudadano() {
        return ciudadano;
    }

    public void setCiudadano(Ciudadano ciudadano) {
        this.ciudadano = ciudadano;
    }

    public PersonalSalud getPersonalSalud() {
        return personalSalud;
    }

    public void setPersonalSalud(PersonalSalud personalSalud) {
        this.personalSalud = personalSalud;
    }

    public InventarioLote getInventario() {
        return inventario;
    }

    public void setInventario(InventarioLote inventario) {
        this.inventario = inventario;
    }

    // ================= BUILDER =================
    public static class VacunacionBuilder {

        private Ciudadano ciudadano;
        private PersonalSalud personalSalud;
        private InventarioLote inventario;
        private NumeroDosis dosis;
        private LocalDateTime fechaAplicacion;
        private String observaciones;
        private boolean reaccionesAdversas;

        public VacunacionBuilder ciudadano(Ciudadano ciudadano) {
            this.ciudadano = ciudadano;
            return this;
        }

        public VacunacionBuilder personalSalud(PersonalSalud personalSalud) {
            this.personalSalud = personalSalud;
            return this;
        }

        public VacunacionBuilder inventario(InventarioLote inventario) {
            this.inventario = inventario;
            return this;
        }

        public VacunacionBuilder dosis(NumeroDosis dosis) {
            this.dosis = dosis;
            return this;
        }

        public VacunacionBuilder fechaAplicacion(LocalDateTime fechaAplicacion) {
            this.fechaAplicacion = fechaAplicacion;
            return this;
        }

        public VacunacionBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public VacunacionBuilder reaccionesAdversas(boolean reaccionesAdversas) {
            this.reaccionesAdversas = reaccionesAdversas;
            return this;
        }

        

        public Vacunacion build() {
            Vacunacion v = new Vacunacion();
            v.ciudadano = this.ciudadano;
            v.personalSalud = this.personalSalud;
            v.inventario = this.inventario;
            v.dosis = this.dosis;
            v.fechaAplicacion = this.fechaAplicacion;
            v.observaciones = this.observaciones;
            v.reaccionesAdversas = this.reaccionesAdversas;
            return v;
        }
    }
}
