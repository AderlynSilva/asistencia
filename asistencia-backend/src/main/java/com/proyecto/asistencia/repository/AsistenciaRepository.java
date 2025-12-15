package com.proyecto.asistencia.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.asistencia.model.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    @Query("""
           SELECT a
           FROM Asistencia a
           WHERE a.idUsuario = :idUsuario
             AND a.fecha BETWEEN :fechaInicio AND :fechaFin
           ORDER BY a.fecha ASC, a.idAsistencia ASC
           """)
    List<Asistencia> buscarHistorial(
            @Param("idUsuario") Long idUsuario,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
