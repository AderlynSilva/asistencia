package com.proyecto.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteAsistenciaPeriodoDto {
    private Long idAsistencia;
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String fecha;
    private String horaCheckin;
    private String horaCheckout;
    private String estado;
}
