package com.proyecto.asistencia.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.asistencia.dto.AsistenciaHistorialDto;
import com.proyecto.asistencia.dto.OperacionResponseDto;
import com.proyecto.asistencia.service.AsistenciaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AsistenciaServiceImpl implements AsistenciaService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OperacionResponseDto registrarCheckin(Long idUsuario, int toleranciaMin) {

        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("PKG_ASISTENCIA.PR_REGISTRAR_CHECKIN");

        sp.registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tolerancia_min", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cod_result", Integer.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_msg_result", String.class, ParameterMode.OUT);

        sp.setParameter("p_id_usuario", idUsuario);
        sp.setParameter("p_tolerancia_min", toleranciaMin);

        sp.execute();

        Integer cod = (Integer) sp.getOutputParameterValue("p_cod_result");
        String msg = (String) sp.getOutputParameterValue("p_msg_result");

        return new OperacionResponseDto(cod != null ? cod : -1, msg);
    }

    @Override
    public OperacionResponseDto registrarCheckout(Long idUsuario) {

        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("PKG_ASISTENCIA.PR_REGISTRAR_CHECKOUT");

        sp.registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cod_result", Integer.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_msg_result", String.class, ParameterMode.OUT);

        sp.setParameter("p_id_usuario", idUsuario);

        sp.execute();

        Integer cod = (Integer) sp.getOutputParameterValue("p_cod_result");
        String msg = (String) sp.getOutputParameterValue("p_msg_result");

        return new OperacionResponseDto(cod != null ? cod : -1, msg);
    }

    @Override
    public List<AsistenciaHistorialDto> obtenerHistorial(Long idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {

        String jpql =
                "SELECT new com.proyecto.asistencia.dto.AsistenciaHistorialDto(" +
                "a.idAsistencia, a.idUsuario, a.fecha, a.horaCheckin, " +
                "a.horaCheckout, a.estado, a.observacion) " +
                "FROM Asistencia a " +
                "WHERE a.idUsuario = :idUsuario " +
                "AND a.fecha BETWEEN :fechaInicio AND :fechaFin " +
                "ORDER BY a.fecha ASC, a.idAsistencia ASC";

        TypedQuery<AsistenciaHistorialDto> query =
                entityManager.createQuery(jpql, AsistenciaHistorialDto.class);

        query.setParameter("idUsuario", idUsuario);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        return query.getResultList();
    }
}
