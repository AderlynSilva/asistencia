package com.proyecto.asistencia.service;

import java.util.List;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;

public interface JustificacionConsultaService {
    List<JustificacionPendienteDto> listarPendientes();
}
