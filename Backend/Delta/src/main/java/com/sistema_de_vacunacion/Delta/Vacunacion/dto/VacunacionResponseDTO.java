package com.sistema_de_vacunacion.Delta.vacunacion.dto;

import java.time.LocalDate;

import com.sistema_de_vacunacion.Delta.vacuna.enums.NumeroDosis;

public class VacunacionResponseDTO {

    private Integer idVacunacion;
    private String vacuna;
    private String numeroLote;
    private NumeroDosis dosis;
    private LocalDate fechaAplicacion;
    private String aplicadoPor;
    private String observaciones;
    private boolean reaccionesAdversas;

    public VacunacionResponseDTO() {}

    public static VacunacionResponseDTOBuilder builder() {
        return new VacunacionResponseDTOBuilder();
    }

    public Integer getIdVacunacion() { return idVacunacion; }
    public void setIdVacunacion(Integer idVacunacion) { this.idVacunacion = idVacunacion; }

    public String getVacuna() { return vacuna; }
    public void setVacuna(String vacuna) { this.vacuna = vacuna; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public NumeroDosis getDosis() { return dosis; }
    public void setDosis(NumeroDosis dosis) { this.dosis = dosis; }

    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

    public String getAplicadoPor() { return aplicadoPor; }
    public void setAplicadoPor(String aplicadoPor) { this.aplicadoPor = aplicadoPor; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isReaccionesAdversas() { return reaccionesAdversas; }
    public void setReaccionesAdversas(boolean reaccionesAdversas) { this.reaccionesAdversas = reaccionesAdversas; }

    public static class VacunacionResponseDTOBuilder {
        private Integer idVacunacion;
        private String vacuna;
        private String numeroLote;
        private NumeroDosis dosis;
        private LocalDate fechaAplicacion;
        private String aplicadoPor;
        private String observaciones;
        private boolean reaccionesAdversas;

        public VacunacionResponseDTOBuilder idVacunacion(Integer idVacunacion) { this.idVacunacion = idVacunacion; return this; }
        public VacunacionResponseDTOBuilder vacuna(String vacuna) { this.vacuna = vacuna; return this; }
        public VacunacionResponseDTOBuilder numeroLote(String numeroLote) { this.numeroLote = numeroLote; return this; }
        public VacunacionResponseDTOBuilder dosis(NumeroDosis dosis) { this.dosis = dosis; return this; }
        public VacunacionResponseDTOBuilder fechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; return this; }
        public VacunacionResponseDTOBuilder aplicadoPor(String aplicadoPor) { this.aplicadoPor = aplicadoPor; return this; }
        public VacunacionResponseDTOBuilder observaciones(String observaciones) { this.observaciones = observaciones; return this; }
        public VacunacionResponseDTOBuilder reaccionesAdversas(boolean reaccionesAdversas) { this.reaccionesAdversas = reaccionesAdversas; return this; }

        public VacunacionResponseDTO build() {
            VacunacionResponseDTO dto = new VacunacionResponseDTO();
            dto.idVacunacion = this.idVacunacion;
            dto.vacuna = this.vacuna;
            dto.numeroLote = this.numeroLote;
            dto.dosis = this.dosis;
            dto.fechaAplicacion = this.fechaAplicacion;
            dto.aplicadoPor = this.aplicadoPor;
            dto.observaciones = this.observaciones;
            dto.reaccionesAdversas = this.reaccionesAdversas;
            return dto;
        }
    }
}