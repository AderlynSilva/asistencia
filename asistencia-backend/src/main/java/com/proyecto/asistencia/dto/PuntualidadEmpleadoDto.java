package com.proyecto.asistencia.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PuntualidadEmpleadoDto {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private Long totalRegistros;
    private Long totalPuntual;
    private Long totalTardanza;
    private BigDecimal porcPuntualidad;

    // Constructor compatible con JPQL (ROUND suele retornar Double)
    public PuntualidadEmpleadoDto(
            Long idUsuario,
            String nombres,
            String apellidos,
            Long totalRegistros,
            Long totalPuntual,
            Long totalTardanza,
            Double porcPuntualidad) {

        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.totalRegistros = totalRegistros;
        this.totalPuntual = totalPuntual;
        this.totalTardanza = totalTardanza;
        this.porcPuntualidad = porcPuntualidad != null ? BigDecimal.valueOf(porcPuntualidad) : BigDecimal.ZERO;
    }
}
