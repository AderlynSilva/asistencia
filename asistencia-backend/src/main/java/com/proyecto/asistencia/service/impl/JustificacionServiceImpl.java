package com.proyecto.asistencia.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.asistencia.dto.JustificacionPendienteDto;
import com.proyecto.asistencia.dto.JustificacionRequestDto;
import com.proyecto.asistencia.dto.OperacionResponseDto;
import com.proyecto.asistencia.repository.JustificacionQueryRepository;
import com.proyecto.asistencia.service.JustificacionService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JustificacionServiceImpl implements JustificacionService {

    @PersistenceContext
    private EntityManager entityManager;

    private final JustificacionQueryRepository justificacionQueryRepository;

    @Override
    @Transactional
    public OperacionResponseDto registrarJustificacion(JustificacionRequestDto request) {

        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("PKG_JUSTIFICACION.PR_REGISTRAR_JUSTIFICACION");

        sp.registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_asistencia", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tipo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_motivo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cod_result", Integer.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_msg_result", String.class, ParameterMode.OUT);

        sp.setParameter("p_id_usuario", request.getIdUsuario());
        sp.setParameter("p_id_asistencia", request.getIdAsistencia());
        sp.setParameter("p_tipo", request.getTipo());
        sp.setParameter("p_motivo", request.getMotivo());

        sp.execute();

        Integer cod = (Integer) sp.getOutputParameterValue("p_cod_result");
        String msg = (String) sp.getOutputParameterValue("p_msg_result");

        return new OperacionResponseDto(cod != null ? cod : -1, msg);
    }

    @Override
    @Transactional
    public OperacionResponseDto aprobarJustificacion(Long idJustificacion, String nuevoEstado, String usuarioRevisor) {

        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("PKG_JUSTIFICACION.PR_APROBAR_JUSTIFICACION");

        sp.registerStoredProcedureParameter("p_id_justificacion", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nuevo_estado", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_usr_revisa", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cod_result", Integer.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_msg_result", String.class, ParameterMode.OUT);

        sp.setParameter("p_id_justificacion", idJustificacion);
        sp.setParameter("p_nuevo_estado", nuevoEstado);
        sp.setParameter("p_usr_revisa", usuarioRevisor);

        sp.execute();

        Integer cod = (Integer) sp.getOutputParameterValue("p_cod_result");
        String msg = (String) sp.getOutputParameterValue("p_msg_result");

        return new OperacionResponseDto(cod != null ? cod : -1, msg);
    }

    @Override
    public List<JustificacionPendienteDto> listarPendientes() {
        return justificacionQueryRepository.listarPendientes();
    }
}
