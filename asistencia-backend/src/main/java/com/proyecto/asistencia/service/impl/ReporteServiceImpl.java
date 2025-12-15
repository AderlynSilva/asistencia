package com.proyecto.asistencia.service.impl;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.asistencia.dto.PuntualidadEmpleadoDto;
import com.proyecto.asistencia.dto.ReporteAsistenciaPeriodoDto;
import com.proyecto.asistencia.service.ReporteService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ReporteServiceImpl implements ReporteService {

    @PersistenceContext
    private EntityManager entityManager;

    // ✅ JPQL (puntualidad)
    @Override
    public List<PuntualidadEmpleadoDto> reportePuntualidad(LocalDate fechaInicio, LocalDate fechaFin) {

        String jpql =
            "SELECT new com.proyecto.asistencia.dto.PuntualidadEmpleadoDto(" +
            "u.idUsuario, " +
            "u.nombres, " +
            "u.apellidos, " +
            "COUNT(a), " +
            "SUM(CASE WHEN a.estado = 'PRESENTE' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.estado = 'TARDANZA' THEN 1 ELSE 0 END), " +
            "ROUND((SUM(CASE WHEN a.estado = 'PRESENTE' THEN 1 ELSE 0 END) * 100.0) / COUNT(a), 2) " +
            ") " +
            "FROM Asistencia a " +
            "JOIN Usuario u ON a.idUsuario = u.idUsuario " +
            "WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY u.idUsuario, u.nombres, u.apellidos " +
            "ORDER BY u.idUsuario";

        TypedQuery<PuntualidadEmpleadoDto> query =
                entityManager.createQuery(jpql, PuntualidadEmpleadoDto.class);

        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        return query.getResultList();
    }

    // ✅ SP con cursor (asistencias por periodo)
    @Override
    public List<ReporteAsistenciaPeriodoDto> reporteAsistenciaPeriodo(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idUsuario) {

        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("PKG_REPORTES.PR_REPORTE_ASISTENCIA_PERIODO");

        sp.registerStoredProcedureParameter("p_fecha_ini", java.sql.Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_fecha_fin", java.sql.Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cursor", ResultSet.class, ParameterMode.REF_CURSOR);

        sp.setParameter("p_fecha_ini", java.sql.Date.valueOf(fechaInicio));
        sp.setParameter("p_fecha_fin", java.sql.Date.valueOf(fechaFin));
        sp.setParameter("p_id_usuario", idUsuario); // null permitido (según tu PL/SQL)

        sp.execute();

        ResultSet rs = (ResultSet) sp.getOutputParameterValue("p_cursor");

        List<ReporteAsistenciaPeriodoDto> result = new ArrayList<>();
        try {
            while (rs.next()) {
                result.add(new ReporteAsistenciaPeriodoDto(
                        rs.getLong("ID_ASISTENCIA"),
                        rs.getLong("ID_USUARIO"),
                        rs.getString("NOMBRES"),
                        rs.getString("APELLIDOS"),
                        rs.getString("FECHA"),
                        rs.getString("HORA_CHECKIN"),
                        rs.getString("HORA_CHECKOUT"),
                        rs.getString("ESTADO")
                ));
            }
            rs.close();
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo cursor de reporteAsistenciaPeriodo: " + e.getMessage(), e);
        }

        return result;
    }
}
