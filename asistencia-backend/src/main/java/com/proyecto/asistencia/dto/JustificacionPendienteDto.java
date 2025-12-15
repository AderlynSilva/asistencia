package com.proyecto.asistencia.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JustificacionPendienteDto {

    private Long idJustificacion;
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String tipo;
    private String motivo;
    private String estado;
    private LocalDateTime fecSolicitud;
}
