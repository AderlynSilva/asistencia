package com.proyecto.asistencia.service;

import java.time.LocalDate;
import java.util.List;

import com.proyecto.asistencia.dto.PuntualidadEmpleadoDto;
import com.proyecto.asistencia.dto.ReporteAsistenciaPeriodoDto;

public interface ReporteService {

    List<PuntualidadEmpleadoDto> reportePuntualidad(LocalDate fechaInicio, LocalDate fechaFin);

    List<ReporteAsistenciaPeriodoDto> reporteAsistenciaPeriodo(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idUsuario
    );
}
