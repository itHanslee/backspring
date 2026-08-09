package com.sistema_de_vacunacion.Delta.vacunacion;

import com.sistema_de_vacunacion.Delta.common.exception.RecursoNoEncontradoException;
import com.sistema_de_vacunacion.Delta.usuario.Ciudadano;
import com.sistema_de_vacunacion.Delta.usuario.CiudadanoRepository;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSalud;
import com.sistema_de_vacunacion.Delta.usuario.PersonalSaludRepository;
import com.sistema_de_vacunacion.Delta.vacuna.Vacuna;
import com.sistema_de_vacunacion.Delta.vacuna.VacunaRepository;
import com.sistema_de_vacunacion.Delta.vacunacion.dto.RegistrarVacunacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VacunacionService {

    private final VacunaRepository vacunaRepository;
    private final VacunacionRepository vacunacionRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final PersonalSaludRepository personalSaludRepository;

    @Transactional
    public void registrarAplicacion(RegistrarVacunacionDTO dto, String emailPersonalSalud) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dto.getIdCiudadano())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado"));

        // Cambia esto en VacunacionService.java:
        PersonalSalud personal = personalSaludRepository.findPersonalActivoPorEmail(emailPersonalSalud)
        .orElseThrow(() -> new RecursoNoEncontradoException("Personal de salud no encontrado o inactivo"));

        // Busca correctamente la entidad Vacuna usando su repositorio
        Vacuna vacuna = vacunaRepository.findById(dto.getIdVacuna())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna no encontrada"));

        Vacunacion vacunacion = Vacunacion.builder()
                .ciudadano(ciudadano)
                .personalSalud(personal)
                .vacuna(vacuna)
                .lote(dto.getLote())
                .dosis(dto.getDosis())
                .fechaAplicacion(LocalDate.now())
                .observaciones(dto.getObservaciones())
                .build();

        vacunacionRepository.save(vacunacion);
    }
}