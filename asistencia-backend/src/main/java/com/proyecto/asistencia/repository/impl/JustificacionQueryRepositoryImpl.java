package com.proyecto.asistencia.repository.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;
import com.proyecto.asistencia.repository.JustificacionQueryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class JustificacionQueryRepositoryImpl implements JustificacionQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<JustificacionPendienteDto> listarPendientes() {

        String sql = """
            SELECT
              id_justificacion,
              id_usuario,
              nombres,
              apellidos,
              tipo,
              motivo,
              estado,
              fec_solicitud
            FROM vw_justificaciones_pendientes
            ORDER BY fec_solicitud ASC, id_justificacion ASC
        """;

        Query q = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = (List<Object[]>) q.getResultList();

        List<JustificacionPendienteDto> dtos = new ArrayList<>();
        for (Object[] r : rows) {

            LocalDateTime fecSolicitud = null;
            Object rawFecha = r[7];
            if (rawFecha != null) {
                if (rawFecha instanceof Timestamp ts) {
                    fecSolicitud = ts.toLocalDateTime();
                } else if (rawFecha instanceof java.sql.Date d) {
                    fecSolicitud = d.toLocalDate().atStartOfDay();
                } else {
                    // por si el driver devuelve otro tipo, evitamos romper
                    fecSolicitud = LocalDateTime.parse(rawFecha.toString().replace(" ", "T"));
                }
            }

            dtos.add(new JustificacionPendienteDto(
                    ((Number) r[0]).longValue(),
                    ((Number) r[1]).longValue(),
                    (String) r[2],
                    (String) r[3],
                    (String) r[4],
                    (String) r[5],
                    (String) r[6],
                    fecSolicitud
            ));
        }

        return dtos;
    }
}
