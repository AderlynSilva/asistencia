package com.proyecto.asistencia.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsistenciaHistorialDto {

    private Long idAsistencia;
    private Long idUsuario;
    private LocalDate fecha;
    private LocalDateTime horaCheckin;
    private LocalDateTime horaCheckout;
    private String estado;
    private String observacion;
}
