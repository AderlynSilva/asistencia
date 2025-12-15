package com.proyecto.asistencia.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.proyecto.asistencia.dto.PuntualidadEmpleadoDto;
import com.proyecto.asistencia.dto.ReporteAsistenciaPeriodoDto;
import com.proyecto.asistencia.service.ReporteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/puntualidad")
    public List<PuntualidadEmpleadoDto> puntualidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
        }
        return reporteService.reportePuntualidad(fechaInicio, fechaFin);
    }

    @GetMapping("/asistencias")
    public List<ReporteAsistenciaPeriodoDto> asistencias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idUsuario) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
        }
        return reporteService.reporteAsistenciaPeriodo(fechaInicio, fechaFin, idUsuario);
    }

    @GetMapping("/fechas")
    public List<ReporteAsistenciaPeriodoDto> fechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idUsuario) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
        }
        return reporteService.reporteAsistenciaPeriodo(fechaInicio, fechaFin, idUsuario);
    }
}
