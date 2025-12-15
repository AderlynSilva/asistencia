package com.proyecto.asistencia.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;
import com.proyecto.asistencia.repository.JustificacionQueryRepository;
import com.proyecto.asistencia.service.JustificacionConsultaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JustificacionConsultaServiceImpl implements JustificacionConsultaService {

    private final JustificacionQueryRepository justificacionQueryRepository;

    @Override
    public List<JustificacionPendienteDto> listarPendientes() {
        return justificacionQueryRepository.listarPendientes();
    }
}
