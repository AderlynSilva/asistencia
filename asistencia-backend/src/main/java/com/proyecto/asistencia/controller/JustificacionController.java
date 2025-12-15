package com.proyecto.asistencia.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;
import com.proyecto.asistencia.dto.JustificacionRequestDto;
import com.proyecto.asistencia.dto.OperacionResponseDto;
import com.proyecto.asistencia.service.JustificacionConsultaService;
import com.proyecto.asistencia.service.JustificacionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/justificaciones")
@RequiredArgsConstructor
public class JustificacionController {

    private final JustificacionService justificacionService;
    private final JustificacionConsultaService justificacionConsultaService;

    @PostMapping
    public OperacionResponseDto registrar(@RequestBody JustificacionRequestDto request) {
        return justificacionService.registrarJustificacion(request);
    }

    @PutMapping("/{id}/estado")
    public OperacionResponseDto cambiarEstado(
            @PathVariable("id") Long idJustificacion,
            @RequestParam String estado,
            @RequestParam String usuarioRevisor) {

        return justificacionService.aprobarJustificacion(idJustificacion, estado, usuarioRevisor);
    }

    @GetMapping("/pendientes")
    public List<JustificacionPendienteDto> pendientes() {
        return justificacionConsultaService.listarPendientes();
    }
}
