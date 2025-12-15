package com.proyecto.asistencia.service;

import java.util.List;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;
import com.proyecto.asistencia.dto.JustificacionRequestDto;
import com.proyecto.asistencia.dto.OperacionResponseDto;

public interface JustificacionService {

    OperacionResponseDto registrarJustificacion(JustificacionRequestDto request);

    OperacionResponseDto aprobarJustificacion(
            Long idJustificacion,
            String nuevoEstado,
            String usuarioRevisor
    );

    List<JustificacionPendienteDto> listarPendientes();
}
