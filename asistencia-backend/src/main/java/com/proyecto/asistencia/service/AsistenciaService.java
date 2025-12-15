package com.proyecto.asistencia.service;

import java.time.LocalDate;
import java.util.List;

import com.proyecto.asistencia.dto.AsistenciaHistorialDto;
import com.proyecto.asistencia.dto.OperacionResponseDto;

public interface AsistenciaService {

    OperacionResponseDto registrarCheckin(Long idUsuario, int toleranciaMin);

    OperacionResponseDto registrarCheckout(Long idUsuario);

    List<AsistenciaHistorialDto> obtenerHistorial(
            Long idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
