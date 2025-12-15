package com.proyecto.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperacionResponseDto {
    private int codigo;
    private String mensaje;
}
