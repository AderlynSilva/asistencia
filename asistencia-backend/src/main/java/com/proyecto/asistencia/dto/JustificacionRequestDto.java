package com.proyecto.asistencia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JustificacionRequestDto {

    private Long idUsuario;
    private Long idAsistencia;   // puede ser null si es falta sin checkin
    private String tipo;         // 'TARDANZA' o 'FALTA'
    private String motivo;
}
