package com.proyecto.asistencia.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.proyecto.asistencia.dto.AsistenciaHistorialDto;
import com.proyecto.asistencia.dto.OperacionResponseDto;
import com.proyecto.asistencia.service.AsistenciaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencia")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping("/checkin")
    public OperacionResponseDto checkin(
            @RequestParam Long idUsuario,
            @RequestParam(defaultValue = "10") int toleranciaMin) {
        return asistenciaService.registrarCheckin(idUsuario, toleranciaMin);
    }

    @PostMapping("/checkout")
    public OperacionResponseDto checkout(@RequestParam Long idUsuario) {
        return asistenciaService.registrarCheckout(idUsuario);
    }

    @GetMapping("/historial")
    public List<AsistenciaHistorialDto> historial(
            @RequestParam Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
        }

        return asistenciaService.obtenerHistorial(idUsuario, fechaInicio, fechaFin);
    }
}
