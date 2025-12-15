package com.proyecto.asistencia.repository;

import java.util.List;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;

public interface JustificacionQueryRepository {
    List<JustificacionPendienteDto> listarPendientes();
}
